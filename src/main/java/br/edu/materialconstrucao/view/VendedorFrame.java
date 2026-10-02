package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.VendedorController;
import br.edu.materialconstrucao.model.Vendedor;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Cadastro de vendedores. Clicar numa linha da tabela carrega o vendedor no
 * formulário para edição; "Limpar" volta ao modo de novo cadastro.
 */
final class VendedorFrame extends BaseFrame {

    private final VendedorController controller;

    private final JTextField campoNome = new JTextField(30);
    private final JTextField campoCpf = new JTextField(14);
    private final JButton botaoSalvar = new JButton("Cadastrar");
    private final JButton botaoExcluir = new JButton("Excluir");
    private final DefaultTableModel modelo = modeloTabela("ID", "Nome", "CPF");
    private final JTable tabela = tabela(modelo);

    private List<Vendedor> vendedores = List.of();
    private Vendedor selecionado;

    VendedorFrame(VendedorController controller, HubFrame hub) {
        super("Cadastrar Vendedor", hub);
        this.controller = controller;
        montarTela();
        carregarTabela();
        limparFormulario();
        finalizarMontagem();
    }

    private void montarTela() {
        definirLarguras(tabela, 60, 440, 200);
        alinharADireita(tabela, 0);

        JPanel form = formulario("Dados do vendedor", new String[]{"Nome:", "CPF:"}, campoNome, campoCpf);

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
        vendedores = controller.listar();
        modelo.setRowCount(0);
        for (Vendedor vendedor : vendedores) {
            modelo.addRow(new Object[]{vendedor.id(), vendedor.nome(), Formatos.cpf(vendedor.cpf())});
        }
    }

    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        selecionado = vendedores.get(linha);
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
            mostrarSucesso(novo ? "Vendedor cadastrado." : "Vendedor atualizado.");
            carregarTabela();
            limparFormulario();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        if (selecionado == null || !confirmar("Excluir o vendedor " + selecionado.nome() + "?")) {
            return;
        }
        try {
            controller.excluir(selecionado.id());
            mostrarSucesso("Vendedor excluído.");
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
