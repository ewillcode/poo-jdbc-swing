package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

final class VendasFrame extends BaseFrame {
    private final VendaController controller;private final DefaultTableModel vendasModelo=new DefaultTableModel(new String[]{"ID","Data/hora","Cliente","Vendedor","Total"},0){public boolean isCellEditable(int r,int c){return false;}};private final DefaultTableModel itensModelo=new DefaultTableModel(new String[]{"Produto","Qtd.","Preço unit.","Subtotal"},0){public boolean isCellEditable(int r,int c){return false;}};private final JTable tabelaVendas=new JTable(vendasModelo),tabelaItens=new JTable(itensModelo);
    VendasFrame(Database db,HubFrame hub){super("Listar Vendas",db,hub);controller=new VendaController(db);montar();atualizar();}
    private void montar(){JButton atualizar=new JButton("Atualizar");atualizar.addActionListener(e->atualizar());tabelaVendas.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())mostrarItens();});JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(tabelaVendas),new JScrollPane(tabelaItens));split.setResizeWeight(.55);JPanel sul=new JPanel();sul.add(atualizar);sul.add(voltar());add(split,BorderLayout.CENTER);add(sul,BorderLayout.SOUTH);pack();}
    private void atualizar(){vendasModelo.setRowCount(0);itensModelo.setRowCount(0);DateTimeFormatter f=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");for(Venda v:controller.listar())vendasModelo.addRow(new Object[]{v.id(),v.dataHora().format(f),v.cliente().nome(),v.vendedor().nome(),dinheiro(v.total())});}
    private void mostrarItens(){int r=tabelaVendas.getSelectedRow();if(r<0)return;itensModelo.setRowCount(0);long id=((Number)vendasModelo.getValueAt(r,0)).longValue();for(ItemVenda i:controller.itens(id))itensModelo.addRow(new Object[]{i.produto().nome(),i.quantidade(),dinheiro(i.precoUnitario()),dinheiro(i.subtotal())});}
}
