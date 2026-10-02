package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela {@code cliente}.
 */
public final class ClienteDao {

    private final Database db;

    public ClienteDao(Database db) {
        this.db = db;
    }

    public List<Cliente> listar() {
        return buscar("", "");
    }

    /**
     * Busca clientes cujo nome contém {@code nome} ou cujo CPF contém {@code cpfDigitos}.
     * Se {@code cpfDigitos} estiver vazio, a busca é feita só pelo nome.
     */
    public List<Cliente> buscar(String nome, String cpfDigitos) {
        String sql = """
                SELECT id, nome, cpf
                FROM cliente
                WHERE nome LIKE ? OR (? <> '' AND cpf LIKE ?)
                ORDER BY nome
                """;
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ps.setString(2, cpfDigitos);
            ps.setString(3, "%" + cpfDigitos + "%");

            List<Cliente> clientes = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(new Cliente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf")));
                }
            }
            return clientes;
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    /** Insere o cliente e devolve uma cópia com o id gerado pelo banco. */
    public Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf) VALUES (?, ?)";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.nome());
            ps.setString(2, cliente.cpf());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return new Cliente(chaves.getLong(1), cliente.nome(), cliente.cpf());
            }
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ? WHERE id = ?";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cliente.nome());
            ps.setString(2, cliente.cpf());
            ps.setLong(3, cliente.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void excluir(long id) {
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }
}
