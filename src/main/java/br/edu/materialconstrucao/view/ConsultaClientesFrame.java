package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.model.Cliente;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Consulta de clientes por parte do nome ou do CPF (com ou sem pontuação).
 */
final class ConsultaClientesFrame extends BaseFrame {

    private final ClienteController controller;

    private final JTextField campoBusca = new JTextField(25);
    private final DefaultTableModel modelo = modeloTabela("ID", "Nome", "CPF");
    private final JLabel rotuloResultado = new JLabel();

    ConsultaClientesFrame(ClienteController controller, HubFrame hub) {
        super("Consultar Clientes", hub);
        this.controller = controller;
        montarTela();
        pesquisar();
        finalizarMontagem();
    }

    private void montarTela() {
        JButton botaoPesquisar = new JButton("Pesquisar");
        JButton botaoLimpar = new JButton("Limpar");
        botaoPesquisar.addActionListener(e -> pesquisar());
        campoBusca.addActionListener(e -> pesquisar()); // Enter no campo também pesquisa
        botaoLimpar.addActionListener(e -> {
            campoBusca.setText("");
            pesquisar();
        });

        add(linhaDeBotoes(new JLabel("Nome ou CPF:"), campoBusca, botaoPesquisar, botaoLimpar, botaoVoltar()),
                BorderLayout.NORTH);
        JTable tabela = tabela(modelo);
        definirLarguras(tabela, 60, 440, 200);
        alinharADireita(tabela, 0);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rotuloResultado, BorderLayout.SOUTH);
    }

    private void pesquisar() {
        modelo.setRowCount(0);
        for (Cliente cliente : controller.buscar(campoBusca.getText())) {
            modelo.addRow(new Object[]{cliente.id(), cliente.nome(), Formatos.cpf(cliente.cpf())});
        }
        rotuloResultado.setText(modelo.getRowCount() + " cliente(s) encontrado(s).");
    }
}
