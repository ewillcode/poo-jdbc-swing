package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.VendedorDao;
import br.edu.materialconstrucao.model.Vendedor;
import java.util.List;

/**
 * Regras do cadastro de vendedores.
 */
public final class VendedorController {

    private final VendedorDao dao;

    public VendedorController(VendedorDao dao) {
        this.dao = dao;
    }

    public List<Vendedor> listar() {
        return dao.listar();
    }

    /**
     * Cadastra um vendedor novo (id nulo) ou atualiza um existente.
     */
    public Vendedor salvar(Long id, String nome, String cpf) {
        Vendedor vendedor = new Vendedor(id, Validacoes.nome(nome), Validacoes.cpf(cpf));
        if (id == null) {
            return dao.inserir(vendedor);
        }
        dao.atualizar(vendedor);
        return vendedor;
    }

    public void excluir(long id) {
        dao.excluir(id);
    }
}
