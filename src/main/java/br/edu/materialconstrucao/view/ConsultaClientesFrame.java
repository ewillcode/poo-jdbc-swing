package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.CadastroController;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

final class ConsultaClientesFrame extends BaseFrame {
    private final CadastroController controller;private final JTextField busca=new JTextField(25);private final DefaultTableModel modelo=new DefaultTableModel(new String[]{"ID","Nome","CPF"},0){public boolean isCellEditable(int r,int c){return false;}};
    ConsultaClientesFrame(Database db,HubFrame hub){super("Consultar Clientes",db,hub);controller=new CadastroController(db);JButton pesquisar=new JButton("Pesquisar");pesquisar.addActionListener(e->atualizar());busca.addActionListener(e->atualizar());JPanel topo=new JPanel();topo.add(new JLabel("Nome ou CPF:"));topo.add(busca);topo.add(pesquisar);topo.add(voltar());add(topo,BorderLayout.NORTH);add(new JScrollPane(new JTable(modelo)),BorderLayout.CENTER);atualizar();pack();}
    private void atualizar(){modelo.setRowCount(0);for(Cliente c:controller.clientes(busca.getText()))modelo.addRow(new Object[]{c.id(),c.nome(),c.cpf()});}
}
