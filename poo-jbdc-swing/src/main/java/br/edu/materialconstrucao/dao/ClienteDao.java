package br.edu.materialconstrucao.dao;

import br.edu.materialconstrucao.model.Cliente;
import java.sql.*;
import java.util.*;

public final class ClienteDao {
    private final Database db;
    public ClienteDao(Database db) { this.db = db; }
    public List<Cliente> listar(String termo) {
        String filtro = termo == null ? "" : termo.trim();
        String sql = "SELECT id,nome,cpf FROM cliente WHERE nome LIKE ? OR cpf LIKE ? ORDER BY nome";
        List<Cliente> resultado = new ArrayList<>();
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%"); ps.setString(2, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) resultado.add(mapear(rs)); }
            return resultado;
        } catch (SQLException e) { throw erro(e); }
    }
    public Cliente salvar(Cliente cliente) {
        if (cliente.id() == null) {
            try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement("INSERT INTO cliente(nome,cpf) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cliente.nome()); ps.setString(2, cliente.cpf()); ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); return new Cliente(rs.getLong(1), cliente.nome(), cliente.cpf()); }
            } catch (SQLException e) { throw erro(e); }
        }
        try (Connection c = db.conectar(); PreparedStatement ps = c.prepareStatement("UPDATE cliente SET nome=?,cpf=? WHERE id=?")) {
            ps.setString(1, cliente.nome()); ps.setString(2, cliente.cpf()); ps.setLong(3, cliente.id()); ps.executeUpdate(); return cliente;
        } catch (SQLException e) { throw erro(e); }
    }
    public void excluir(long id) { executarExclusao("DELETE FROM cliente WHERE id=?", id); }
    private Cliente mapear(ResultSet rs) throws SQLException { return new Cliente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf")); }
    private void executarExclusao(String sql, long id) { try (Connection c=db.conectar(); PreparedStatement ps=c.prepareStatement(sql)) { ps.setLong(1,id); ps.executeUpdate(); } catch(SQLException e){ throw erro(e); } }
    private IllegalArgumentException erro(SQLException e) { return new IllegalArgumentException(mensagem(e), e); }
    static String mensagem(SQLException e) { String m=e.getMessage().toLowerCase(); if(m.contains("unique")) return "CPF já cadastrado."; if(m.contains("foreign key")) return "O registro possui vendas vinculadas e não pode ser excluído."; return "Erro ao acessar o banco: " + e.getMessage(); }
}
