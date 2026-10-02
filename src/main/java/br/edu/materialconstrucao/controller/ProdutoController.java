package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.ProdutoDao;
import br.edu.materialconstrucao.model.Produto;
import java.util.List;

/**
 * Regras do cadastro de produtos.
 */
public final class ProdutoController {

    private final ProdutoDao dao;

    public ProdutoController(ProdutoDao dao) {
        this.dao = dao;
    }

    public List<Produto> listar() {
        return dao.listar();
    }

    /**
     * Cadastra um produto novo (id nulo) ou atualiza um existente.
     * O preço vem como texto da tela, por exemplo "25,90".
     */
    public Produto salvar(Long id, String nome, String preco) {
        Produto produto = new Produto(id, Validacoes.nome(nome), Validacoes.preco(preco));
        if (id == null) {
            return dao.inserir(produto);
        }
        dao.atualizar(produto);
        return produto;
    }

    public void excluir(long id) {
        dao.excluir(id);
    }
}
