package br.edu.materialconstrucao.model;

import java.math.BigDecimal;

public record Produto(Long id, String nome, BigDecimal preco) {
    @Override public String toString() { return nome + " - R$ " + preco; }
}
