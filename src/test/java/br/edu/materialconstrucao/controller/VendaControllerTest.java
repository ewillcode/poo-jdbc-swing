package br.edu.materialconstrucao.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.materialconstrucao.model.Cliente;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Produto;
import br.edu.materialconstrucao.model.Venda;
import br.edu.materialconstrucao.model.Vendedor;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VendaControllerTest {

    @TempDir
    Path pasta;

    private BancoDeTeste banco;
    private Cliente ana;
    private Vendedor bruno;
    private Produto cimento;
    private Produto areia;

    @BeforeEach
    void preparar() {
        banco = new BancoDeTeste(pasta);
        ana = banco.clientes.salvar(null, "Ana", "52998224725");
        bruno = banco.vendedores.salvar(null, "Bruno", "45317828791");
        cimento = banco.produtos.salvar(null, "Cimento", "20,00");
        areia = banco.produtos.salvar(null, "Areia", "5,50");
    }

    @Test
    void carrinhoSomaQuantidadeDoMesmoProduto() {
        List<ItemVenda> carrinho = new ArrayList<>();

        banco.vendas.adicionarAoCarrinho(carrinho, cimento, 2);
        banco.vendas.adicionarAoCarrinho(carrinho, cimento, 3);

        assertEquals(1, carrinho.size());
        assertEquals(5, carrinho.getFirst().quantidade());
        assertEquals(0, new BigDecimal("100.00").compareTo(carrinho.getFirst().subtotal()));
    }

    @Test
    void gravaVendaComItensEPreservaPrecoDaEpoca() {
        List<ItemVenda> carrinho = new ArrayList<>();
        banco.vendas.adicionarAoCarrinho(carrinho, cimento, 2);
        banco.vendas.adicionarAoCarrinho(carrinho, areia, 3);

        Venda venda = banco.vendas.finalizar(ana, bruno, carrinho);

        assertEquals(0, new BigDecimal("56.50").compareTo(venda.total()));
        assertEquals(2, banco.vendas.listarItens(venda.id()).size());
        assertEquals(1, banco.vendas.listar().size());

        // reajustar o produto não pode mudar o preço da venda já feita
        banco.produtos.salvar(cimento.id(), "Cimento", "25,00");
        ItemVenda itemCimento = banco.vendas.listarItens(venda.id()).getFirst();
        assertEquals(0, new BigDecimal("20.00").compareTo(itemCimento.precoUnitario()));
    }

    @Test
    void naoFinalizaVendaIncompleta() {
        List<ItemVenda> carrinho = new ArrayList<>();
        banco.vendas.adicionarAoCarrinho(carrinho, cimento, 1);

        assertThrows(IllegalArgumentException.class, () -> banco.vendas.finalizar(null, bruno, carrinho));
        assertThrows(IllegalArgumentException.class, () -> banco.vendas.finalizar(ana, null, carrinho));
        assertThrows(IllegalArgumentException.class, () -> banco.vendas.finalizar(ana, bruno, List.of()));
        assertThrows(IllegalArgumentException.class, () -> banco.vendas.adicionarAoCarrinho(carrinho, null, 1));
        assertThrows(IllegalArgumentException.class, () -> banco.vendas.adicionarAoCarrinho(carrinho, areia, 0));
        assertTrue(banco.vendas.listar().isEmpty());
    }

    @Test
    void bloqueiaExclusaoDeRegistrosLigadosAVenda() {
        List<ItemVenda> carrinho = new ArrayList<>();
        banco.vendas.adicionarAoCarrinho(carrinho, cimento, 1);
        banco.vendas.finalizar(ana, bruno, carrinho);

        RuntimeException erro = assertThrows(RuntimeException.class, () -> banco.clientes.excluir(ana.id()));
        assertEquals("O registro está ligado a uma venda e não pode ser excluído.", erro.getMessage());
        assertThrows(RuntimeException.class, () -> banco.vendedores.excluir(bruno.id()));
        assertThrows(RuntimeException.class, () -> banco.produtos.excluir(cimento.id()));

        // a areia não foi vendida, então pode ser excluída
        banco.produtos.excluir(areia.id());
        assertEquals(1, banco.produtos.listar().size());
    }
}
