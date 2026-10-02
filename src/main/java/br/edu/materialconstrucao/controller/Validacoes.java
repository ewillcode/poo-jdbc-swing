package br.edu.materialconstrucao.controller;

import java.math.BigDecimal;

/**
 * Regras de validação usadas pelos controllers. Cada método devolve o valor
 * já limpo ou lança {@link IllegalArgumentException} com a mensagem para o usuário.
 */
final class Validacoes {

    private Validacoes() {
    }

    static String nome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }
        return nome.trim();
    }

    /** Aceita o CPF com ou sem pontuação e devolve só os 11 dígitos. */
    static String cpf(String cpf) {
        String digitos = somenteDigitos(cpf);
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("Informe um CPF com 11 dígitos.");
        }
        if (!cpfTemDigitosVerificadoresCorretos(digitos)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        return digitos;
    }

    /**
     * Aceita "25,90", "25.90" ou "1.250,00" e devolve o valor com 2 casas decimais.
     */
    static BigDecimal preco(String preco) {
        if (preco == null || preco.isBlank()) {
            throw new IllegalArgumentException("Informe o preço.");
        }
        String texto = preco.trim().replace("R$", "").trim();
        if (texto.contains(",")) {
            // formato brasileiro: o ponto separa milhar e a vírgula separa os centavos
            texto = texto.replace(".", "").replace(",", ".");
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um preço válido. Exemplo: 25,90");
        }
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("O preço pode ter no máximo 2 casas decimais.");
        }
        return valor.setScale(2);
    }

    static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    /** Algoritmo oficial do CPF: os 2 últimos dígitos são calculados a partir dos 9 primeiros. */
    private static boolean cpfTemDigitosVerificadoresCorretos(String cpf) {
        boolean todosIguais = cpf.chars().distinct().count() == 1;
        if (todosIguais) {
            return false;
        }
        return digitoVerificador(cpf, 9) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoVerificador(String cpf, int quantidadeDigitos) {
        int soma = 0;
        int peso = quantidadeDigitos + 1;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += (cpf.charAt(i) - '0') * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
