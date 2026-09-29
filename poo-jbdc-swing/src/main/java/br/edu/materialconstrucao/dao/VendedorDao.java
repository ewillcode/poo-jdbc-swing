package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Vendedor;
import java.sql.*;
import java.util.*;

public final class VendedorDao {
    private final Database db;
    public VendedorDao(Database db) { this.db = db; }
    public List<Vendedor> listar() { List<Vendedor> r=new ArrayList<>(); try(Connection c=db.conectar(); PreparedStatement ps=c.prepareStatement("SELECT id,nome,cpf FROM vendedor ORDER BY nome"); ResultSet rs=ps.executeQuery()){while(rs.next())r.add(new Vendedor(rs.getLong(1),rs.getString(2),rs.getString(3))); return r;}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);} }
    public Vendedor salvar(Vendedor v) { String sql=v.id()==null?"INSERT INTO vendedor(nome,cpf) VALUES (?,?)":"UPDATE vendedor SET nome=?,cpf=? WHERE id=?"; try(Connection c=db.conectar(); PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){ps.setString(1,v.nome());ps.setString(2,v.cpf());if(v.id()!=null){ps.setLong(3,v.id());ps.executeUpdate();return v;}ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){rs.next();return new Vendedor(rs.getLong(1),v.nome(),v.cpf());}}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);} }
    public void excluir(long id){try(Connection c=db.conectar();PreparedStatement ps=c.prepareStatement("DELETE FROM vendedor WHERE id=?")){ps.setLong(1,id);ps.executeUpdate();}catch(SQLException e){throw new IllegalArgumentException(ClienteDao.mensagem(e),e);}}
}
