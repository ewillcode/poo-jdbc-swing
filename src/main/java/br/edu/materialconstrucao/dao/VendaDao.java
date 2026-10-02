package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public final class VendaDao {
    private final Database db;
    public VendaDao(Database db) { this.db = db; }

    public Venda salvar(Cliente cliente, Vendedor vendedor, List<ItemVenda> itens) {
        BigDecimal total = itens.stream().map(ItemVenda::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        try (Connection c = db.conectar()) {
            c.setAutoCommit(false);
            try {
                long idVenda;
                LocalDateTime agora = LocalDateTime.now().withNano(0);
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO venda(data_hora,cliente_id,vendedor_id,total) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, agora.toString()); ps.setLong(2, cliente.id()); ps.setLong(3, vendedor.id()); ps.setBigDecimal(4, total); ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); idVenda = rs.getLong(1); }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO item_venda(venda_id,produto_id,quantidade,preco_unitario,subtotal) VALUES (?,?,?,?,?)")) {
                    for (ItemVenda item : itens) { ps.setLong(1,idVenda); ps.setLong(2,item.produto().id()); ps.setInt(3,item.quantidade()); ps.setBigDecimal(4,item.precoUnitario()); ps.setBigDecimal(5,item.subtotal()); ps.addBatch(); }
                    ps.executeBatch();
                }
                c.commit(); return new Venda(idVenda, agora, cliente, vendedor, total);
            } catch (Exception e) { c.rollback(); throw e; }
        } catch (Exception e) { throw new IllegalArgumentException("Não foi possível finalizar a venda: " + e.getMessage(), e); }
    }

    public List<Venda> listar() {
        String sql = "SELECT v.id,v.data_hora,v.total,c.id cliente_id,c.nome cliente_nome,c.cpf cliente_cpf,ve.id vendedor_id,ve.nome vendedor_nome,ve.cpf vendedor_cpf FROM venda v JOIN cliente c ON c.id=v.cliente_id JOIN vendedor ve ON ve.id=v.vendedor_id ORDER BY v.id DESC";
        List<Venda> r = new ArrayList<>();
        try (Connection c=db.conectar(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) r.add(new Venda(rs.getLong("id"),LocalDateTime.parse(rs.getString("data_hora")),new Cliente(rs.getLong("cliente_id"),rs.getString("cliente_nome"),rs.getString("cliente_cpf")),new Vendedor(rs.getLong("vendedor_id"),rs.getString("vendedor_nome"),rs.getString("vendedor_cpf")),rs.getBigDecimal("total")));
            return r;
        } catch(SQLException e) { throw new IllegalArgumentException("Erro ao listar vendas: " + e.getMessage(),e); }
    }

    public List<ItemVenda> itens(long vendaId) {
        List<ItemVenda> r=new ArrayList<>(); String sql="SELECT i.id,i.quantidade,i.preco_unitario,i.subtotal,p.id produto_id,p.nome,p.preco FROM item_venda i JOIN produto p ON p.id=i.produto_id WHERE i.venda_id=? ORDER BY i.id";
        try(Connection c=db.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,vendaId);try(ResultSet rs=ps.executeQuery()){while(rs.next()){Produto p=new Produto(rs.getLong("produto_id"),rs.getString("nome"),rs.getBigDecimal("preco"));r.add(new ItemVenda(rs.getLong("id"),p,rs.getInt("quantidade"),rs.getBigDecimal("preco_unitario"),rs.getBigDecimal("subtotal")));}}return r;}catch(SQLException e){throw new IllegalArgumentException("Erro ao carregar itens da venda: "+e.getMessage(),e);}
    }
}
