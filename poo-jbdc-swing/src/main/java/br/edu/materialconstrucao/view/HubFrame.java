package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.dao.Database;
import javax.swing.*;
import java.awt.*;

public final class HubFrame extends JFrame {
    private final Database db;
    public HubFrame(Database db) {
        super("Sistema de Material de Construção"); this.db=db; setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(520,430); setLocationRelativeTo(null);
        JPanel painel=new JPanel(new BorderLayout(12,12)); painel.setBorder(BorderFactory.createEmptyBorder(25,35,25,35));
        JLabel titulo=new JLabel("Sistema de Material de Construção",SwingConstants.CENTER); titulo.setFont(titulo.getFont().deriveFont(Font.BOLD,22f));
        JLabel texto=new JLabel("Gerencie cadastros e vendas de forma simples.",SwingConstants.CENTER);
        JPanel topo=new JPanel(new GridLayout(2,1)); topo.add(titulo);topo.add(texto);painel.add(topo,BorderLayout.NORTH);
        JPanel botoes=new JPanel(new GridLayout(3,2,10,10));
        adicionar(botoes,"Cadastrar Cliente",() -> new PessoaFrame("Cadastrar Cliente",db,this,true));
        adicionar(botoes,"Cadastrar Produto",() -> new ProdutoFrame(db,this));
        adicionar(botoes,"Cadastrar Vendedor",() -> new PessoaFrame("Cadastrar Vendedor",db,this,false));
        adicionar(botoes,"Cadastrar Venda",() -> new VendaFrame(db,this));
        adicionar(botoes,"Listar Vendas",() -> new VendasFrame(db,this));
        adicionar(botoes,"Consultar Clientes",() -> new ConsultaClientesFrame(db,this));
        painel.add(botoes,BorderLayout.CENTER); add(painel);
    }
    private void adicionar(JPanel painel,String texto,java.util.function.Supplier<JFrame> tela){JButton b=new JButton(texto);b.addActionListener(e->{setVisible(false);tela.get().setVisible(true);});painel.add(b);}
}
