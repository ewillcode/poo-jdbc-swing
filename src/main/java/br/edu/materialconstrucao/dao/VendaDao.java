package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Cliente;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Produto;
import br.edu.materialconstrucao.model.Venda;
import br.edu.materialconstrucao.model.Vendedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso às tabelas {@code venda} e {@code item_venda}.
 */
public final class VendaDao {

    private final Database db;

    public VendaDao(Database db) {
        this.db = db;
    }

    /**
     * Grava a venda e todos os seus itens numa única transação:
     * se qualquer item falhar, nada é gravado (rollback).
     *
     * @return a venda com o id gerado pelo banco
     */
    public Venda inserir(Venda venda, List<ItemVenda> itens) {
        try (Connection c = db.conectar()) {
            c.setAutoCommit(false);
            try {
                long idVenda = inserirCabecalho(c, venda);
                inserirItens(c, idVenda, itens);
                c.commit();
                return new Venda(idVenda, venda.dataHora(), venda.cliente(), venda.vendedor(), venda.total());
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Não foi possível finalizar a venda: " + e.getMessage(), e);
        }
    }

    private long inserirCabecalho(Connection c, Venda venda) throws SQLException {
        String sql = "INSERT INTO venda (data_hora, cliente_id, vendedor_id, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, venda.dataHora().toString());
            ps.setLong(2, venda.cliente().id());
            ps.setLong(3, venda.vendedor().id());
            ps.setBigDecimal(4, venda.total());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        }
    }

    private void inserirItens(Connection c, long idVenda, List<ItemVenda> itens) throws SQLException {
        String sql = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (ItemVenda item : itens) {
                ps.setLong(1, idVenda);
                ps.setLong(2, item.produto().id());
                ps.setInt(3, item.quantidade());
                ps.setBigDecimal(4, item.precoUnitario());
                ps.setBigDecimal(5, item.subtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Lista todas as vendas, da mais recente para a mais antiga, já com cliente e vendedor. */
    public List<Venda> listar() {
        String sql = """
                SELECT v.id, v.data_hora, v.total,
                       c.id  AS cliente_id,  c.nome  AS cliente_nome,  c.cpf  AS cliente_cpf,
                       ve.id AS vendedor_id, ve.nome AS vendedor_nome, ve.cpf AS vendedor_cpf
                FROM venda v
                JOIN cliente  c  ON c.id  = v.cliente_id
                JOIN vendedor ve ON ve.id = v.vendedor_id
                ORDER BY v.id DESC
                """;
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Venda> vendas = new ArrayList<>();
            while (rs.next()) {
                Cliente cliente = new Cliente(rs.getLong("cliente_id"), rs.getString("cliente_nome"), rs.getString("cliente_cpf"));
                Vendedor vendedor = new Vendedor(rs.getLong("vendedor_id"), rs.getString("vendedor_nome"), rs.getString("vendedor_cpf"));
                LocalDateTime dataHora = LocalDateTime.parse(rs.getString("data_hora"));
                vendas.add(new Venda(rs.getLong("id"), dataHora, cliente, vendedor, rs.getBigDecimal("total")));
            }
            return vendas;
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }

    /** Lista os itens de uma venda, com o preço que foi cobrado na época. */
    public List<ItemVenda> listarItens(long idVenda) {
        String sql = """
                SELECT i.id, i.quantidade, i.preco_unitario, i.subtotal,
                       p.id AS produto_id, p.nome AS produto_nome, p.preco AS produto_preco
                FROM item_venda i
                JOIN produto p ON p.id = i.produto_id
                WHERE i.venda_id = ?
                ORDER BY i.id
                """;
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idVenda);

            List<ItemVenda> itens = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(rs.getLong("produto_id"), rs.getString("produto_nome"), rs.getBigDecimal("produto_preco"));
                    itens.add(new ItemVenda(rs.getLong("id"), produto, rs.getInt("quantidade"),
                            rs.getBigDecimal("preco_unitario"), rs.getBigDecimal("subtotal")));
                }
            }
            return itens;
        } catch (SQLException e) {
            throw Database.traduzirErro(e);
        }
    }
}
