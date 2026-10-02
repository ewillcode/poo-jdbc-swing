package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.*;
import br.edu.materialconstrucao.model.*;
import java.math.BigDecimal;
import java.util.*;

public final class VendaController {
    private final VendaDao vendas;
    public VendaController(Database db) { vendas = new VendaDao(db); }
    public Venda finalizar(Cliente cliente, Vendedor vendedor, List<ItemVenda> itens) {
        if(cliente==null) throw new IllegalArgumentException("Selecione o cliente.");
        if(vendedor==null) throw new IllegalArgumentException("Selecione o vendedor.");
        if(itens==null || itens.isEmpty()) throw new IllegalArgumentException("Inclua ao menos um produto na venda.");
        for(ItemVenda i:itens) if(i.quantidade()<=0 || i.precoUnitario().signum()<=0) throw new IllegalArgumentException("Há item inválido no carrinho.");
        return vendas.salvar(cliente,vendedor,itens);
    }
    public ItemVenda criarItem(Produto produto, int quantidade) {
        if(produto==null) throw new IllegalArgumentException("Selecione um produto.");
        if(quantidade<=0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        BigDecimal subtotal=produto.preco().multiply(BigDecimal.valueOf(quantidade));
        return new ItemVenda(null,produto,quantidade,produto.preco(),subtotal);
    }
    public List<Venda> listar(){return vendas.listar();} public List<ItemVenda> itens(long vendaId){return vendas.itens(vendaId);}
}
