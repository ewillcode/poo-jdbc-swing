package br.edu.materialconstrucao.model;

import java.math.BigDecimal;

public record ItemVenda(Long id, Produto produto, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) { }
