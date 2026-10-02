package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Vendedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela {@code vendedor}.
 */
public final class VendedorDao {

    private final Database db;

    public VendedorDao(Database db) {
        this.db = db;
    }

    public List<Vendedor> listar() {
        String sql = "SELECT id, nome, cpf FROM vendedor ORDER BY nome";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Vendedor> vendedores = new ArrayList<>();
            while (rs.next()) {
                vendedores.add(new Vendedor(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf")));
            }
            return vendedores;
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    /** Insere o vendedor e devolve uma cópia com o id gerado pelo banco. */
    public Vendedor inserir(Vendedor vendedor) {
        String sql = "INSERT INTO vendedor (nome, cpf) VALUES (?, ?)";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vendedor.nome());
            ps.setString(2, vendedor.cpf());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return new Vendedor(chaves.getLong(1), vendedor.nome(), vendedor.cpf());
            }
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void atualizar(Vendedor vendedor) {
        String sql = "UPDATE vendedor SET nome = ?, cpf = ? WHERE id = ?";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, vendedor.nome());
            ps.setString(2, vendedor.cpf());
            ps.setLong(3, vendedor.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void excluir(long id) {
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement("DELETE FROM vendedor WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }
}
