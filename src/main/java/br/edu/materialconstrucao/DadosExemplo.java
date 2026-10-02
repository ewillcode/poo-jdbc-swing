package br.edu.materialconstrucao;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.controller.ProdutoController;
import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.controller.VendedorController;
import br.edu.materialconstrucao.model.Cliente;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Produto;
import br.edu.materialconstrucao.model.Vendedor;
import java.util.ArrayList;
import java.util.List;

/**
 * Cadastra alguns registros de exemplo quando o banco está vazio,
 * para o sistema já abrir com dados para demonstração.
 * Usa os controllers, então os dados passam pelas mesmas validações das telas.
 */
final class DadosExemplo {

    private final ClienteController clientes;
    private final VendedorController vendedores;
    private final ProdutoController produtos;
    private final VendaController vendas;

    DadosExemplo(ClienteController clientes, VendedorController vendedores,
                 ProdutoController produtos, VendaController vendas) {
        this.clientes = clientes;
        this.vendedores = vendedores;
        this.produtos = produtos;
        this.vendas = vendas;
    }

    void inserir() {
        Cliente ana = clientes.salvar(null, "Ana Souza", "529.982.247-25");
        Cliente carlos = clientes.salvar(null, "Carlos Lima", "111.444.777-35");
        clientes.salvar(null, "Juliana Pereira", "390.533.447-05");

        Vendedor bruno = vendedores.salvar(null, "Bruno Alves", "453.178.287-91");
        Vendedor marina = vendedores.salvar(null, "Marina Costa", "714.602.380-01");

        Produto cimento = produtos.salvar(null, "Cimento CP II 50kg", "38,90");
        Produto areia = produtos.salvar(null, "Areia média (saco 20kg)", "6,50");
        Produto tijolo = produtos.salvar(null, "Tijolo cerâmico 8 furos", "1,20");
        Produto vergalhao = produtos.salvar(null, "Vergalhão CA-50 10mm (12m)", "54,00");
        produtos.salvar(null, "Tinta acrílica branca 18L", "289,90");

        List<ItemVenda> carrinhoAna = new ArrayList<>();
        vendas.adicionarAoCarrinho(carrinhoAna, cimento, 10);
        vendas.adicionarAoCarrinho(carrinhoAna, areia, 20);
        vendas.finalizar(ana, bruno, carrinhoAna);

        List<ItemVenda> carrinhoCarlos = new ArrayList<>();
        vendas.adicionarAoCarrinho(carrinhoCarlos, tijolo, 500);
        vendas.adicionarAoCarrinho(carrinhoCarlos, vergalhao, 4);
        vendas.finalizar(carlos, marina, carrinhoCarlos);
    }
}
