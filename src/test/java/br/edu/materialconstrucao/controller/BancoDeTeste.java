package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.dao.ClienteDao;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.dao.ProdutoDao;
import br.edu.materialconstrucao.dao.VendaDao;
import br.edu.materialconstrucao.dao.VendedorDao;
import java.nio.file.Path;

/**
 * Cria um banco SQLite novo e vazio numa pasta temporária para cada teste.
 */
final class BancoDeTeste {

    final ClienteController clientes;
    final VendedorController vendedores;
    final ProdutoController produtos;
    final VendaController vendas;

    BancoDeTeste(Path pastaTemporaria) {
        Database db = new Database(pastaTemporaria.resolve("teste.db"));
        db.inicializar();
        clientes = new ClienteController(new ClienteDao(db));
        vendedores = new VendedorController(new VendedorDao(db));
        produtos = new ProdutoController(new ProdutoDao(db));
        vendas = new VendaController(new VendaDao(db));
    }
}
