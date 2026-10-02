package br.edu.materialconstrucao.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.materialconstrucao.model.Produto;
import java.math.BigDecimal;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProdutoControllerTest {

    @TempDir
    Path pasta;

    private ProdutoController produtos;

    @BeforeEach
    void preparar() {
        produtos = new BancoDeTeste(pasta).produtos;
    }

    @Test
    void aceitaPrecoNoFormatoBrasileiro() {
        assertEquals(new BigDecimal("25.90"), produtos.salvar(null, "Cimento", "25,90").preco());
        assertEquals(new BigDecimal("1250.00"), produtos.salvar(null, "Betoneira", "1.250,00").preco());
        assertEquals(new BigDecimal("7.50"), produtos.salvar(null, "Areia", "7.5").preco());
    }

    @Test
    void rejeitaPrecoInvalido() {
        IllegalArgumentException maisDeDuasCasas = assertThrows(IllegalArgumentException.class,
                () -> produtos.salvar(null, "Cimento", "10,555"));
        assertEquals("O preço pode ter no máximo 2 casas decimais.", maisDeDuasCasas.getMessage());

        assertThrows(IllegalArgumentException.class, () -> produtos.salvar(null, "Cimento", "0"));
        assertThrows(IllegalArgumentException.class, () -> produtos.salvar(null, "Cimento", "-5"));
        assertThrows(IllegalArgumentException.class, () -> produtos.salvar(null, "Cimento", "abc"));
        assertThrows(IllegalArgumentException.class, () -> produtos.salvar(null, "Cimento", ""));
    }

    @Test
    void atualizaPreco() {
        Produto cimento = produtos.salvar(null, "Cimento", "25,90");

        produtos.salvar(cimento.id(), "Cimento", "27,00");

        assertEquals(0, new BigDecimal("27.00").compareTo(produtos.listar().getFirst().preco()));
    }
}
