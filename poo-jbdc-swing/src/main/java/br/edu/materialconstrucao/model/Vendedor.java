package br.edu.materialconstrucao.model;

public record Vendedor(Long id, String nome, String cpf) {
    @Override public String toString() { return nome + " - " + cpf; }
}
