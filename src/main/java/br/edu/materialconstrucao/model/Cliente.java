package br.edu.materialconstrucao.model;

/**
 * Cliente da loja. O CPF é guardado só com os 11 dígitos, sem pontuação.
 * O id fica nulo enquanto o cliente ainda não foi salvo no banco.
 */
public record Cliente(Long id, String nome, String cpf) {
}
