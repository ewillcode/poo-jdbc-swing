package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.controller.ProdutoController;
import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.controller.VendedorController;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Tela inicial: apresenta o sistema e abre as demais telas.
 * Enquanto outra tela está aberta, o Hub fica escondido.
 */
public final class HubFrame extends JFrame {

    private final ClienteController clientes;
    private final VendedorController vendedores;
    private final ProdutoController produtos;
    private final VendaController vendas;

    public HubFrame(ClienteController clientes, VendedorController vendedores,
                    ProdutoController produtos, VendaController vendas) {
        super("Sistema de Material de Construção");
        this.clientes = clientes;
        this.vendedores = vendedores;
        this.produtos = produtos;
        this.vendas = vendas;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        montarTela();
        setSize(560, 440);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel painel = new JPanel(new BorderLayout(16, 16));
        painel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel titulo = new JLabel("Sistema de Material de Construção", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitulo = new JLabel("Cadastre clientes, vendedores e produtos e registre as vendas da loja.",
                SwingConstants.CENTER);

        JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 6));
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);
        painel.add(cabecalho, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(3, 2, 12, 12));
        adicionarBotao(botoes, "Cadastrar Cliente", () -> new ClienteFrame(clientes, this));
        adicionarBotao(botoes, "Cadastrar Produto", () -> new ProdutoFrame(produtos, this));
        adicionarBotao(botoes, "Cadastrar Vendedor", () -> new VendedorFrame(vendedores, this));
        adicionarBotao(botoes, "Cadastrar Venda", () -> new VendaFrame(vendas, clientes, vendedores, produtos, this));
        adicionarBotao(botoes, "Listar Vendas", () -> new ListarVendasFrame(vendas, this));
        adicionarBotao(botoes, "Consultar Clientes", () -> new ConsultaClientesFrame(clientes, this));
        painel.add(botoes, BorderLayout.CENTER);

        setContentPane(painel);
    }

    /** Cria um botão que esconde o Hub e abre a tela criada por {@code abrirTela}. */
    private void adicionarBotao(JPanel painel, String texto, Supplier<JFrame> abrirTela) {
        JButton botao = new JButton(texto);
        botao.setFont(botao.getFont().deriveFont(Font.BOLD, 14f));
        botao.addActionListener(e -> {
            setVisible(false);
            abrirTela.get().setVisible(true);
        });
        painel.add(botao);
    }
}
