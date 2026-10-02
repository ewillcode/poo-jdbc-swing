package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.controller.ProdutoController;
import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.controller.VendedorController;
import br.edu.materialconstrucao.model.Cliente;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Produto;
import br.edu.materialconstrucao.model.Venda;
import br.edu.materialconstrucao.model.Vendedor;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Registro de uma venda: escolhe cliente e vendedor, monta o carrinho
 * de produtos e grava tudo ao clicar em "Finalizar Venda".
 */
final class VendaFrame extends BaseFrame {

    private final VendaController vendas;
    private final ClienteController clientes;
    private final VendedorController vendedores;
    private final ProdutoController produtos;

    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JComboBox<Vendedor> comboVendedor = new JComboBox<>();
    private final JComboBox<Produto> comboProduto = new JComboBox<>();
    private final JSpinner campoQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JLabel rotuloTotal = new JLabel();
    private final DefaultTableModel modeloCarrinho = modeloTabela("Produto", "Qtd.", "Preço unit.", "Subtotal");
    private final JTable tabelaCarrinho = tabela(modeloCarrinho);

    private final List<ItemVenda> carrinho = new ArrayList<>();

    VendaFrame(VendaController vendas, ClienteController clientes, VendedorController vendedores,
               ProdutoController produtos, HubFrame hub) {
        super("Cadastrar Venda", hub);
        this.vendas = vendas;
        this.clientes = clientes;
        this.vendedores = vendedores;
        this.produtos = produtos;
        montarTela();
        carregarCombos();
        atualizarCarrinho();
        finalizarMontagem();
    }

    private void montarTela() {
        definirLarguras(tabelaCarrinho, 400, 80, 120, 120);
        alinharADireita(tabelaCarrinho, 1, 2, 3);
        exibirComo(comboCliente, c -> c.nome() + " (CPF " + Formatos.cpf(c.cpf()) + ")");
        exibirComo(comboVendedor, v -> v.nome());
        exibirComo(comboProduto, p -> p.nome() + " - " + Formatos.dinheiro(p.preco()));

        JPanel dadosVenda = formulario("Dados da venda", new String[]{"Cliente:", "Vendedor:"},
                comboCliente, comboVendedor);

        JButton botaoAdicionar = new JButton("Adicionar ao carrinho");
        JButton botaoRemover = new JButton("Remover item");
        botaoAdicionar.addActionListener(e -> adicionarItem());
        botaoRemover.addActionListener(e -> removerItem());

        JPanel linhaProduto = new JPanel(new BorderLayout(8, 0));
        linhaProduto.add(comboProduto, BorderLayout.CENTER);
        linhaProduto.add(linhaDeBotoes(new JLabel("Qtd.:"), campoQuantidade, botaoAdicionar, botaoRemover), BorderLayout.EAST);
        JPanel itens = formulario("Produtos", new String[]{"Produto:"}, linhaProduto);

        JPanel topo = new JPanel(new BorderLayout(0, 8));
        topo.add(dadosVenda, BorderLayout.NORTH);
        topo.add(itens, BorderLayout.CENTER);

        JButton botaoFinalizar = new JButton("Finalizar Venda");
        botaoFinalizar.setFont(botaoFinalizar.getFont().deriveFont(Font.BOLD));
        botaoFinalizar.addActionListener(e -> finalizar());
        rotuloTotal.setFont(rotuloTotal.getFont().deriveFont(Font.BOLD, 16f));

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(rotuloTotal, BorderLayout.WEST);
        rodape.add(linhaDeBotoes(botaoFinalizar, botaoVoltar()), BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarCombos() {
        comboCliente.removeAllItems();
        comboVendedor.removeAllItems();
        comboProduto.removeAllItems();
        clientes.listar().forEach(comboCliente::addItem);
        vendedores.listar().forEach(comboVendedor::addItem);
        produtos.listar().forEach(comboProduto::addItem);

        // cliente e vendedor começam sem seleção para obrigar uma escolha consciente
        comboCliente.setSelectedIndex(-1);
        comboVendedor.setSelectedIndex(-1);
    }

    private void adicionarItem() {
        try {
            Produto produto = (Produto) comboProduto.getSelectedItem();
            int quantidade = (Integer) campoQuantidade.getValue();
            vendas.adicionarAoCarrinho(carrinho, produto, quantidade);
            campoQuantidade.setValue(1);
            atualizarCarrinho();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void removerItem() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha < 0) {
            mostrarErro(new IllegalArgumentException("Selecione um item do carrinho."));
            return;
        }
        carrinho.remove(linha);
        atualizarCarrinho();
    }

    private void atualizarCarrinho() {
        modeloCarrinho.setRowCount(0);
        for (ItemVenda item : carrinho) {
            modeloCarrinho.addRow(new Object[]{
                    item.produto().nome(),
                    item.quantidade(),
                    Formatos.dinheiro(item.precoUnitario()),
                    Formatos.dinheiro(item.subtotal())
            });
        }
        rotuloTotal.setText("Total: " + Formatos.dinheiro(vendas.calcularTotal(carrinho)));
    }

    private void finalizar() {
        try {
            Cliente cliente = (Cliente) comboCliente.getSelectedItem();
            Vendedor vendedor = (Vendedor) comboVendedor.getSelectedItem();
            Venda venda = vendas.finalizar(cliente, vendedor, carrinho);
            mostrarSucesso("Venda nº " + venda.id() + " finalizada. Total: " + Formatos.dinheiro(venda.total()));

            carrinho.clear();
            atualizarCarrinho();
            carregarCombos();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }
}
