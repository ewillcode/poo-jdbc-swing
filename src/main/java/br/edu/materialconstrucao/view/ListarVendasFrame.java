package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.model.ItemVenda;
import br.edu.materialconstrucao.model.Venda;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Lista as vendas realizadas. Ao selecionar uma venda, os itens dela
 * aparecem na tabela de baixo.
 */
final class ListarVendasFrame extends BaseFrame {

    private final VendaController controller;

    private final DefaultTableModel modeloVendas = modeloTabela("Nº", "Data/hora", "Cliente", "Vendedor", "Total");
    private final DefaultTableModel modeloItens = modeloTabela("Produto", "Qtd.", "Preço unit.", "Subtotal");
    private final JTable tabelaVendas = tabela(modeloVendas);
    private final JTable tabelaItens = tabela(modeloItens);

    private List<Venda> vendas = List.of();

    ListarVendasFrame(VendaController controller, HubFrame hub) {
        super("Listar Vendas", hub);
        this.controller = controller;
        montarTela();
        carregarVendas();
        finalizarMontagem();
    }

    private void montarTela() {
        definirLarguras(tabelaVendas, 50, 130, 220, 180, 120);
        alinharADireita(tabelaVendas, 0, 4);
        definirLarguras(tabelaItens, 400, 80, 120, 120);
        alinharADireita(tabelaItens, 1, 2, 3);
        tabelaVendas.setPreferredScrollableViewportSize(new Dimension(720, 200));
        tabelaItens.setPreferredScrollableViewportSize(new Dimension(720, 150));

        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarItensDaSelecionada();
            }
        });

        JScrollPane painelVendas = new JScrollPane(tabelaVendas);
        painelVendas.setBorder(BorderFactory.createTitledBorder("Vendas"));
        JScrollPane painelItens = new JScrollPane(tabelaItens);
        painelItens.setBorder(BorderFactory.createTitledBorder("Itens da venda selecionada"));

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelVendas, painelItens);
        divisao.setResizeWeight(0.55);

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.addActionListener(e -> carregarVendas());

        add(divisao, BorderLayout.CENTER);
        add(linhaDeBotoes(botaoAtualizar, botaoVoltar()), BorderLayout.SOUTH);
    }

    private void carregarVendas() {
        vendas = controller.listar();
        modeloVendas.setRowCount(0);
        modeloItens.setRowCount(0);
        for (Venda venda : vendas) {
            modeloVendas.addRow(new Object[]{
                    venda.id(),
                    Formatos.dataHora(venda.dataHora()),
                    venda.cliente().nome(),
                    venda.vendedor().nome(),
                    Formatos.dinheiro(venda.total())
            });
        }
    }

    private void mostrarItensDaSelecionada() {
        int linha = tabelaVendas.getSelectedRow();
        modeloItens.setRowCount(0);
        if (linha < 0) {
            return;
        }
        Venda venda = vendas.get(linha);
        for (ItemVenda item : controller.listarItens(venda.id())) {
            modeloItens.addRow(new Object[]{
                    item.produto().nome(),
                    item.quantidade(),
                    Formatos.dinheiro(item.precoUnitario()),
                    Formatos.dinheiro(item.subtotal())
            });
        }
    }
}
