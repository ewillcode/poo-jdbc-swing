package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela {@code produto}.
 */
public final class ProdutoDao {

    private final Database db;

    public ProdutoDao(Database db) {
        this.db = db;
    }

    public List<Produto> listar() {
        String sql = "SELECT id, nome, preco FROM produto ORDER BY nome";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Produto> produtos = new ArrayList<>();
            while (rs.next()) {
                produtos.add(new Produto(rs.getLong("id"), rs.getString("nome"), rs.getBigDecimal("preco")));
            }
            return produtos;
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    /** Insere o produto e devolve uma cópia com o id gerado pelo banco. */
    public Produto inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco) VALUES (?, ?)";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produto.nome());
            ps.setBigDecimal(2, produto.preco());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return new Produto(chaves.getLong(1), produto.nome(), produto.preco());
            }
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ? WHERE id = ?";
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, produto.nome());
            ps.setBigDecimal(2, produto.preco());
            ps.setLong(3, produto.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    public void excluir(long id) {
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement("DELETE FROM produto WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }
}
