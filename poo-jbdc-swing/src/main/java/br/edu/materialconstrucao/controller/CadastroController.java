package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.*;
import br.edu.materialconstrucao.model.*;
import java.math.BigDecimal;
import java.util.List;

public final class CadastroController {
    private final ClienteDao clientes; private final ProdutoDao produtos; private final VendedorDao vendedores;
    public CadastroController(Database db) { clientes=new ClienteDao(db); produtos=new ProdutoDao(db); vendedores=new VendedorDao(db); }
    public List<Cliente> clientes(String termo) { return clientes.listar(termo); }
    public List<Produto> produtos() { return produtos.listar(); }
    public List<Vendedor> vendedores() { return vendedores.listar(); }
    public Cliente salvarCliente(Long id,String nome,String cpf){return clientes.salvar(new Cliente(id,nomeObrigatorio(nome),cpfValido(cpf)));}
    public Vendedor salvarVendedor(Long id,String nome,String cpf){return vendedores.salvar(new Vendedor(id,nomeObrigatorio(nome),cpfValido(cpf)));}
    public Produto salvarProduto(Long id,String nome,String preco){return produtos.salvar(new Produto(id,nomeObrigatorio(nome),precoValido(preco)));}
    public void excluirCliente(long id){clientes.excluir(id);} public void excluirProduto(long id){produtos.excluir(id);} public void excluirVendedor(long id){vendedores.excluir(id);}
    private String nomeObrigatorio(String nome){if(nome==null||nome.trim().isEmpty())throw new IllegalArgumentException("Informe o nome.");return nome.trim();}
    private String cpfValido(String cpf){String limpo=cpf==null?"":cpf.replaceAll("\\D","");if(!limpo.matches("\\d{11}"))throw new IllegalArgumentException("Informe um CPF com 11 dígitos.");return limpo;}
    private BigDecimal precoValido(String preco){try{BigDecimal valor=new BigDecimal(preco.trim().replace(",","."));if(valor.signum()<=0)throw new NumberFormatException();return valor.setScale(2);}catch(Exception e){throw new IllegalArgumentException("Informe um preço maior que zero.");}}
}
