package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.ProdutoController;
import br.edu.materialconstrucao.model.Produto;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Cadastro de produtos. Clicar numa linha da tabela carrega o produto no
 * formulário para edição; "Limpar" volta ao modo de novo cadastro.
 */
final class ProdutoFrame extends BaseFrame {

    private final ProdutoController controller;

    private final JTextField campoNome = new JTextField(30);
    private final JTextField campoPreco = new JTextField(10);
    private final JButton botaoSalvar = new JButton("Cadastrar");
    private final JButton botaoExcluir = new JButton("Excluir");
    private final DefaultTableModel modelo = modeloTabela("ID", "Nome", "Preço");
    private final JTable tabela = tabela(modelo);

    private List<Produto> produtos = List.of();
    private Produto selecionado;

    ProdutoFrame(ProdutoController controller, HubFrame hub) {
        super("Cadastrar Produto", hub);
        this.controller = controller;
        montarTela();
        carregarTabela();
        limparFormulario();
        finalizarMontagem();
    }

    private void montarTela() {
        definirLarguras(tabela, 60, 480, 160);
        alinharADireita(tabela, 0, 2);

        JPanel form = formulario("Dados do produto", new String[]{"Nome:", "Preço (R$):"}, campoNome, campoPreco);

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
        produtos = controller.listar();
        modelo.setRowCount(0);
        for (Produto produto : produtos) {
            modelo.addRow(new Object[]{produto.id(), produto.nome(), Formatos.dinheiro(produto.preco())});
        }
    }

    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        selecionado = produtos.get(linha);
        campoNome.setText(selecionado.nome());
        campoPreco.setText(Formatos.decimal(selecionado.preco()));
        botaoSalvar.setText("Salvar alterações");
        botaoExcluir.setEnabled(true);
    }

    private void salvar() {
        try {
            boolean novo = selecionado == null;
            Long id = novo ? null : selecionado.id();
            controller.salvar(id, campoNome.getText(), campoPreco.getText());
            mostrarSucesso(novo ? "Produto cadastrado." : "Produto atualizado.");
            carregarTabela();
            limparFormulario();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        if (selecionado == null || !confirmar("Excluir o produto " + selecionado.nome() + "?")) {
            return;
        }
        try {
            controller.excluir(selecionado.id());
            mostrarSucesso("Produto excluído.");
            carregarTabela();
            limparFormulario();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void limparFormulario() {
        selecionado = null;
        campoNome.setText("");
        campoPreco.setText("");
        tabela.clearSelection();
        botaoSalvar.setText("Cadastrar");
        botaoExcluir.setEnabled(false);
        campoNome.requestFocusInWindow();
    }
}
