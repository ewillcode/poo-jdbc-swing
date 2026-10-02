package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.ClienteDao;
import br.edu.materialconstrucao.model.Cliente;
import java.util.List;

/**
 * Regras do cadastro e da consulta de clientes.
 */
public final class ClienteController {

    private final ClienteDao dao;

    public ClienteController(ClienteDao dao) {
        this.dao = dao;
    }

    public List<Cliente> listar() {
        return dao.listar();
    }

    /** Busca por parte do nome ou do CPF. O CPF pode ser digitado com ou sem pontuação. */
    public List<Cliente> buscar(String termo) {
        String texto = termo == null ? "" : termo.trim();
        return dao.buscar(texto, Validacoes.somenteDigitos(texto));
    }

    /**
     * Cadastra um cliente novo (id nulo) ou atualiza um existente.
     */
    public Cliente salvar(Long id, String nome, String cpf) {
        Cliente cliente = new Cliente(id, Validacoes.nome(nome), Validacoes.cpf(cpf));
        if (id == null) {
            return dao.inserir(cliente);
        }
        dao.atualizar(cliente);
        return cliente;
    }

    public void excluir(long id) {
        dao.excluir(id);
    }
}
