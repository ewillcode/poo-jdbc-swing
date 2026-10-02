package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.model.Cliente;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Cadastro de clientes. Clicar numa linha da tabela carrega o cliente no
 * formulário para edição; "Limpar" volta ao modo de novo cadastro.
 */
final class ClienteFrame extends BaseFrame {

    private final ClienteController controller;

    private final JTextField campoNome = new JTextField(30);
    private final JTextField campoCpf = new JTextField(14);
    private final JButton botaoSalvar = new JButton("Cadastrar");
    private final JButton botaoExcluir = new JButton("Excluir");
    private final DefaultTableModel modelo = modeloTabela("ID", "Nome", "CPF");
    private final JTable tabela = tabela(modelo);

    private List<Cliente> clientes = List.of();
    private Cliente selecionado;

    ClienteFrame(ClienteController controller, HubFrame hub) {
        super("Cadastrar Cliente", hub);
        this.controller = controller;
        montarTela();
        carregarTabela();
        limparFormulario();
        finalizarMontagem();
    }

    private void montarTela() {
        definirLarguras(tabela, 60, 440, 200);
        alinharADireita(tabela, 0);

        JPanel form = formulario("Dados do cliente", new String[]{"Nome:", "CPF:"}, campoNome, campoCpf);

        JButton botaoLimpar = new JButton("Limpar");
        botaoSalvar.addActionListener(e -> salvar());
        botaoLimpar.addActionListener(e -> limparFormulario());
        botaoExcluir.addActionListener(e -> excluir());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionado();
            }
        });

        JPanel topo = new JPanel(new BorderLayout(0, 8));
        topo.add(form, BorderLayout.CENTER);
        topo.add(linhaDeBotoes(botaoSalvar, botaoLimpar, botaoExcluir, botaoVoltar()), BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarTabela() {
        clientes = controller.listar();
        modelo.setRowCount(0);
        for (Cliente cliente : clientes) {
            modelo.addRow(new Object[]{cliente.id(), cliente.nome(), Formatos.cpf(cliente.cpf())});
        }
    }

    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        selecionado = clientes.get(linha);
        campoNome.setText(selecionado.nome());
        campoCpf.setText(Formatos.cpf(selecionado.cpf()));
        botaoSalvar.setText("Salvar alterações");
        botaoExcluir.setEnabled(true);
    }

    private void salvar() {
        try {
            boolean novo = selecionado == null;
            Long id = novo ? null : selecionado.id();
            controller.salvar(id, campoNome.getText(), campoCpf.getText());
            mostrarSucesso(novo ? "Cliente cadastrado." : "Cliente atualizado.");
            carregarTabela();
            limparFormulario();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        if (selecionado == null || !confirmar("Excluir o cliente " + selecionado.nome() + "?")) {
            return;
        }
        try {
            controller.excluir(selecionado.id());
            mostrarSucesso("Cliente excluído.");
            carregarTabela();
            limparFormulario();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void limparFormulario() {
        selecionado = null;
        campoNome.setText("");
        campoCpf.setText("");
        tabela.clearSelection();
        botaoSalvar.setText("Cadastrar");
        botaoExcluir.setEnabled(false);
        campoNome.requestFocusInWindow();
    }
}
