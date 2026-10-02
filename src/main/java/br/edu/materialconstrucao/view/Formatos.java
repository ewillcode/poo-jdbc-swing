package br.edu.materialconstrucao.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formata valores para exibição nas telas, no padrão brasileiro.
 */
final class Formatos {

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formatos() {
    }

    /** 1250.5 → "R$ 1.250,50" */
    static String dinheiro(BigDecimal valor) {
        return MOEDA.format(valor);
    }

    /** 25.9 → "25,90", usado para preencher o campo de preço na edição. */
    static String decimal(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }

    /** "12345678909" → "123.456.789-09" */
    static String cpf(String digitos) {
        if (digitos == null || digitos.length() != 11) {
            return digitos;
        }
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    static String dataHora(LocalDateTime dataHora) {
        return dataHora.format(DATA_HORA);
    }
}
