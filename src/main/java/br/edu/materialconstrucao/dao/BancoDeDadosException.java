package br.edu.materialconstrucao.dao;

/**
 * Erro ao ler ou gravar no banco. A mensagem já vem pronta para mostrar ao usuário.
 */
public class BancoDeDadosException extends RuntimeException {

    public BancoDeDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
