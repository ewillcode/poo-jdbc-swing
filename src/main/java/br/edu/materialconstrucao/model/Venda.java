package br.edu.materialconstrucao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cabeçalho de uma venda: quando aconteceu, quem comprou, quem vendeu e o total.
 * Os produtos vendidos ficam em {@link ItemVenda}.
 */
public record Venda(Long id, LocalDateTime dataHora, Cliente cliente, Vendedor vendedor, BigDecimal total) {
}
