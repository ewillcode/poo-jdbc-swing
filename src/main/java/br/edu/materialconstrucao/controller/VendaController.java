package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.VendaDao;
import br.edu.materialconstrucao.model.Cliente;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Produto;
import br.edu.materialconstrucao.model.Venda;
import br.edu.materialconstrucao.model.Vendedor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regras da venda: montagem do carrinho, cálculo do total e gravação.
 */
public final class VendaController {

    private final VendaDao dao;

    public VendaController(VendaDao dao) {
        this.dao = dao;
    }

    /**
     * Coloca o produto no carrinho. Se ele já estiver lá, soma a quantidade
     * em vez de criar uma linha repetida.
     */
    public void adicionarAoCarrinho(List<ItemVenda> carrinho, Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Selecione um produto.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        for (int i = 0; i < carrinho.size(); i++) {
            ItemVenda existente = carrinho.get(i);
            if (existente.produto().id().equals(produto.id())) {
                carrinho.set(i, criarItem(produto, existente.quantidade() + quantidade));
                return;
            }
        }
        carrinho.add(criarItem(produto, quantidade));
    }

    public BigDecimal calcularTotal(List<ItemVenda> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    /** Valida e grava a venda com a data e hora atuais. */
    public Venda finalizar(Cliente cliente, Vendedor vendedor, List<ItemVenda> itens) {
        if (cliente == null) {
            throw new IllegalArgumentException("Selecione o cliente.");
        }
        if (vendedor == null) {
            throw new IllegalArgumentException("Selecione o vendedor.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Inclua ao menos um produto na venda.");
        }

        LocalDateTime agora = LocalDateTime.now().withNano(0);
        Venda venda = new Venda(null, agora, cliente, vendedor, calcularTotal(itens));
        return dao.inserir(venda, itens);
    }

    public List<Venda> listar() {
        return dao.listar();
    }

    public List<ItemVenda> listarItens(long idVenda) {
        return dao.listarItens(idVenda);
    }

    /** O preço unitário é copiado do produto agora, para não mudar se o produto for reajustado depois. */
    private ItemVenda criarItem(Produto produto, int quantidade) {
        BigDecimal subtotal = produto.preco().multiply(BigDecimal.valueOf(quantidade));
        return new ItemVenda(null, produto, quantidade, produto.preco(), subtotal);
    }
}
