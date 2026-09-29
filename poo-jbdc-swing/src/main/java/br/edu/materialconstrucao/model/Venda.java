package br.edu.materialconstrucao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Venda(Long id, LocalDateTime dataHora, Cliente cliente, Vendedor vendedor, BigDecimal total) { }
