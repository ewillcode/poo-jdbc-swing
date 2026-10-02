package br.edu.materialconstrucao.model;

/**
 * Vendedor responsável pelas vendas. O CPF é guardado só com os 11 dígitos.
 * O id fica nulo enquanto o vendedor ainda não foi salvo no banco.
 */
public record Vendedor(Long id, String nome, String cpf) {
}
