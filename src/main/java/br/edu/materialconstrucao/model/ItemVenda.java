package br.edu.materialconstrucao.model;

import java.math.BigDecimal;

/**
 * Um produto dentro de uma venda.
 * O preço unitário é copiado do produto no momento da venda, então uma mudança
 * de preço depois não altera as vendas que já foram feitas.
 */
public record ItemVenda(Long id, Produto produto, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {
}
