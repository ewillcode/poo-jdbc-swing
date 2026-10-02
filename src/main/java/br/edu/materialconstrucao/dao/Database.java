package br.edu.materialconstrucao.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private final String url;

    public Database(Path arquivo) { this.url = "jdbc:sqlite:" + arquivo.toAbsolutePath(); }
    public static Database padrao() { return new Database(Path.of("data", "material_construcao.db")); }

    public Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(url);
        try (Statement st = conexao.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
        return conexao;
    }

    public void inicializar() {
        try {
            Path caminho = Path.of(url.substring("jdbc:sqlite:".length()));
            if (caminho.getParent() != null) Files.createDirectories(caminho.getParent());
            try (Connection c = conectar(); Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS cliente (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, cpf TEXT NOT NULL UNIQUE)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS vendedor (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, cpf TEXT NOT NULL UNIQUE)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS produto (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, preco NUMERIC NOT NULL CHECK(preco > 0))");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS venda (id INTEGER PRIMARY KEY AUTOINCREMENT, data_hora TEXT NOT NULL, cliente_id INTEGER NOT NULL, vendedor_id INTEGER NOT NULL, total NUMERIC NOT NULL CHECK(total >= 0), FOREIGN KEY(cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT, FOREIGN KEY(vendedor_id) REFERENCES vendedor(id) ON DELETE RESTRICT)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS item_venda (id INTEGER PRIMARY KEY AUTOINCREMENT, venda_id INTEGER NOT NULL, produto_id INTEGER NOT NULL, quantidade INTEGER NOT NULL CHECK(quantidade > 0), preco_unitario NUMERIC NOT NULL CHECK(preco_unitario > 0), subtotal NUMERIC NOT NULL CHECK(subtotal > 0), UNIQUE(venda_id, produto_id), FOREIGN KEY(venda_id) REFERENCES venda(id) ON DELETE RESTRICT, FOREIGN KEY(produto_id) REFERENCES produto(id) ON DELETE RESTRICT)");
            }
        } catch (Exception e) { throw new IllegalStateException("Não foi possível inicializar o banco de dados.", e); }
    }
}
