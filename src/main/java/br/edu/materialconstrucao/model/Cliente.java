package br.edu.materialconstrucao.model;

public record Cliente(Long id, String nome, String cpf) {
    @Override public String toString() { return nome + " - " + cpf; }
}
