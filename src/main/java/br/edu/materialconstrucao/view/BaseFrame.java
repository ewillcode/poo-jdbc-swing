package br.edu.materialconstrucao.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Base de todas as telas abertas pelo Hub. Ao fechar a tela (pelo botão
 * "Voltar ao Hub" ou pelo X da janela), o Hub volta a aparecer.
 */
abstract class BaseFrame extends JFrame {

    private static final String TITULO_SISTEMA = "Sistema de Material de Construção";

    BaseFrame(String titulo, HubFrame hub) {
        super(titulo + " - " + TITULO_SISTEMA);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(780, 520));

        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(conteudo);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                hub.setVisible(true);
            }
        });
    }

    /** Ajusta o tamanho da janela ao conteúdo e centraliza na tela. */
    protected void finalizarMontagem() {
        pack();
        setLocationRelativeTo(null);
    }

    protected JButton botaoVoltar() {
        JButton voltar = new JButton("Voltar ao Hub");
        voltar.addActionListener(e -> dispose());
        return voltar;
    }

    protected static JPanel linhaDeBotoes(JComponent... componentes) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (JComponent componente : componentes) {
            painel.add(componente);
        }
        return painel;
    }

    /** Monta um formulário com os rótulos à esquerda e os campos à direita. */
    protected static JPanel formulario(String titulo, String[] rotulos, JComponent... campos) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder(titulo));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        for (int linha = 0; linha < campos.length; linha++) {
            c.gridy = linha;

            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            painel.add(new JLabel(rotulos[linha]), c);

            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            painel.add(campos[linha], c);
        }
        return painel;
    }

    /** Modelo de tabela somente leitura: os dados são alterados pelo formulário, não pela tabela. */
    protected static DefaultTableModel modeloTabela(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    protected static JTable tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setPreferredScrollableViewportSize(new Dimension(720, 260));
        return tabela;
    }

    /** Define a largura de cada coluna; o espaço que sobra é dividido na mesma proporção. */
    protected static void definirLarguras(JTable tabela, int... larguras) {
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    /** Alinha à direita as colunas de números e valores, como numa planilha. */
    protected static void alinharADireita(JTable tabela, int... colunas) {
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna : colunas) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
    }

    /** Define o texto que cada item mostra dentro do combo (por padrão seria o toString). */
    protected static <T> void exibirComo(JComboBox<T> combo, Function<T, String> texto) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean comFoco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, comFoco);
                setText(valor == null ? "Selecione..." : texto.apply((T) valor));
                return this;
            }
        });
    }

    protected void mostrarErro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    protected void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, TITULO_SISTEMA, JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean confirmar(String pergunta) {
        int resposta = JOptionPane.showConfirmDialog(this, pergunta, "Confirmar", JOptionPane.YES_NO_OPTION);
        return resposta == JOptionPane.YES_OPTION;
    }
}
