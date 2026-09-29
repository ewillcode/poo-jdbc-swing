package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Produto;
import java.sql.*;
import java.util.*;

public final class ProdutoDao {
    private final Database db;
    public ProdutoDao(Database db) { this.db = db; }
    public List<Produto> listar() { List<Produto> r=new ArrayList<>();try(Connection c=db.conectar();PreparedStatement ps=c.prepareStatement("SELECT id,nome,preco FROM produto ORDER BY nome");ResultSet rs=ps.executeQuery()){while(rs.next())r.add(new Produto(rs.getLong(1),rs.getString(2),rs.getBigDecimal(3)));return r;}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);} }
    public Produto salvar(Produto p){String sql=p.id()==null?"INSERT INTO produto(nome,preco) VALUES (?,?)":"UPDATE produto SET nome=?,preco=? WHERE id=?";try(Connection c=db.conectar();PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){ps.setString(1,p.nome());ps.setBigDecimal(2,p.preco());if(p.id()!=null){ps.setLong(3,p.id());ps.executeUpdate();return p;}ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){rs.next();return new Produto(rs.getLong(1),p.nome(),p.preco());}}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);}}
    public void excluir(long id){try(Connection c=db.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM produto WHERE id=?")){ps.setLong(1,id);ps.executeUpdate();}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);}}
}
