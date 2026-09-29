package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.CadastroController;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.Produto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

final class ProdutoFrame extends BaseFrame {
    private final CadastroController controller;private final JTextField nome=new JTextField(),preco=new JTextField();private final DefaultTableModel modelo=new DefaultTableModel(new String[]{"ID","Nome","Preço"},0){public boolean isCellEditable(int r,int c){return false;}};private final JTable tabela=new JTable(modelo);private Long selecionado;
    ProdutoFrame(Database db,HubFrame hub){super("Cadastrar Produto",db,hub);controller=new CadastroController(db);montar();atualizar();}
    private void montar(){JPanel form=new JPanel(new GridLayout(2,2,8,8));form.setBorder(BorderFactory.createTitledBorder("Dados"));form.add(new JLabel("Nome:"));form.add(nome);form.add(new JLabel("Preço (R$):"));form.add(preco);JPanel a=new JPanel();JButton salvar=new JButton("Salvar"),limpar=new JButton("Limpar"),excluir=new JButton("Excluir");a.add(salvar);a.add(limpar);a.add(excluir);a.add(voltar());salvar.addActionListener(e->salvar());limpar.addActionListener(e->limpar());excluir.addActionListener(e->excluir());tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});JPanel n=new JPanel(new BorderLayout());n.add(form);n.add(a,BorderLayout.SOUTH);add(n,BorderLayout.NORTH);add(new JScrollPane(tabela));pack();}
    private void atualizar(){modelo.setRowCount(0);for(Produto p:controller.produtos())modelo.addRow(new Object[]{p.id(),p.nome(),p.preco()});}
    private void selecionar(){int r=tabela.getSelectedRow();if(r<0)return;selecionado=((Number)modelo.getValueAt(r,0)).longValue();nome.setText((String)modelo.getValueAt(r,1));preco.setText(modelo.getValueAt(r,2).toString());}
    private void salvar(){try{controller.salvarProduto(selecionado,nome.getText(),preco.getText());sucesso(selecionado==null?"Cadastro realizado.":"Cadastro atualizado.");limpar();atualizar();}catch(Exception e){erro(e);}}
    private void excluir(){if(selecionado==null){erro(new IllegalArgumentException("Selecione um registro."));return;}if(JOptionPane.showConfirmDialog(this,"Excluir o produto selecionado?","Confirmar",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;try{controller.excluirProduto(selecionado);limpar();atualizar();sucesso("Produto excluído.");}catch(Exception e){erro(e);}}
    private void limpar(){selecionado=null;nome.setText("");preco.setText("");tabela.clearSelection();}
}
