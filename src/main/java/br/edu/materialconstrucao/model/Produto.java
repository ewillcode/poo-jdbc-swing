package br.edu.materialconstrucao.model;

import java.math.BigDecimal;

/**
 * Produto vendido pela loja, com o preço atual.
 * O id fica nulo enquanto o produto ainda não foi salvo no banco.
 */
public record Produto(Long id, String nome, BigDecimal preco) {
}
