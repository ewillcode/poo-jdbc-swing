package br.edu.materialconstrucao.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abre conexões com o banco SQLite e cria as tabelas do sistema.
 */
public final class Database {

    private static final String[] TABELAS = {
            """
            CREATE TABLE IF NOT EXISTS cliente (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf  TEXT NOT NULL UNIQUE
            )""",
            """
            CREATE TABLE IF NOT EXISTS vendedor (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf  TEXT NOT NULL UNIQUE
            )""",
            """
            CREATE TABLE IF NOT EXISTS produto (
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                nome  TEXT    NOT NULL,
                preco NUMERIC NOT NULL CHECK (preco > 0)
            )""",
            """
            CREATE TABLE IF NOT EXISTS venda (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                data_hora   TEXT    NOT NULL,
                cliente_id  INTEGER NOT NULL,
                vendedor_id INTEGER NOT NULL,
                total       NUMERIC NOT NULL CHECK (total >= 0),
                FOREIGN KEY (cliente_id)  REFERENCES cliente (id)  ON DELETE RESTRICT,
                FOREIGN KEY (vendedor_id) REFERENCES vendedor (id) ON DELETE RESTRICT
            )""",
            """
            CREATE TABLE IF NOT EXISTS item_venda (
                id             INTEGER PRIMARY KEY AUTOINCREMENT,
                venda_id       INTEGER NOT NULL,
                produto_id     INTEGER NOT NULL,
                quantidade     INTEGER NOT NULL CHECK (quantidade > 0),
                preco_unitario NUMERIC NOT NULL CHECK (preco_unitario > 0),
                subtotal       NUMERIC NOT NULL CHECK (subtotal > 0),
                UNIQUE (venda_id, produto_id),
                FOREIGN KEY (venda_id)   REFERENCES venda (id)   ON DELETE RESTRICT,
                FOREIGN KEY (produto_id) REFERENCES produto (id) ON DELETE RESTRICT
            )"""
    };

    private final Path arquivo;

    public Database(Path arquivo) {
        this.arquivo = arquivo.toAbsolutePath();
    }

    /** Banco usado pela aplicação, na pasta {@code data/} de onde o programa foi aberto. */
    public static Database padrao() {
        return new Database(Path.of("data", "material_construcao.db"));
    }

    public Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + arquivo);
        try (Statement st = conexao.createStatement()) {
            // No SQLite as chaves estrangeiras vêm desligadas e precisam ser ativadas a cada conexão
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conexao;
    }

    /** Cria a pasta e as tabelas, caso ainda não existam. */
    public void inicializar() {
        try {
            Files.createDirectories(arquivo.getParent());
            try (Connection c = conectar(); Statement st = c.createStatement()) {
                for (String tabela : TABELAS) {
                    st.executeUpdate(tabela);
                }
            }
        } catch (IOException | SQLException e) {
            throw new BancoDeDadosException("Não foi possível inicializar o banco de dados.", e);
        }
    }

    /** Indica se ainda não há nenhum cliente, vendedor ou produto cadastrado. */
    public boolean estaVazio() {
        String sql = """
                SELECT (SELECT COUNT(*) FROM cliente)
                     + (SELECT COUNT(*) FROM vendedor)
                     + (SELECT COUNT(*) FROM produto)
                """;
        try (Connection c = conectar(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw traduzirErro(e);
        }
    }

    /** Converte o erro técnico do SQLite numa mensagem que pode ser mostrada ao usuário. */
    static BancoDeDadosException traduzirErro(SQLException e) {
        String detalhe = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        if (detalhe.contains("unique") && detalhe.contains("cpf")) {
            return new BancoDeDadosException("CPF já cadastrado.", e);
        }
        if (detalhe.contains("foreign key")) {
            return new BancoDeDadosException("O registro está ligado a uma venda e não pode ser excluído.", e);
        }
        return new BancoDeDadosException("Erro ao acessar o banco de dados: " + e.getMessage(), e);
    }
}
