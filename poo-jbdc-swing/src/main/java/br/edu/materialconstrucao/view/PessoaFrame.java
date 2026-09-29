package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.CadastroController;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

final class PessoaFrame extends BaseFrame {
    private final CadastroController controller; private final boolean cliente; private final JTextField nome=new JTextField(); private final JTextField cpf=new JTextField();
    private final DefaultTableModel modelo=new DefaultTableModel(new String[]{"ID","Nome","CPF"},0){public boolean isCellEditable(int r,int c){return false;}}; private final JTable tabela=new JTable(modelo); private Long selecionado;
    PessoaFrame(String titulo,Database db,HubFrame hub,boolean cliente){super(titulo,db,hub);this.cliente=cliente;controller=new CadastroController(db);montar();atualizar();}
    private void montar(){
        JPanel form=new JPanel(new GridLayout(2,2,8,8));form.setBorder(BorderFactory.createTitledBorder("Dados"));form.add(new JLabel("Nome:"));form.add(nome);form.add(new JLabel("CPF:"));form.add(cpf);
        JPanel acoes=new JPanel(); JButton salvar=new JButton("Salvar");JButton limpar=new JButton("Limpar");JButton excluir=new JButton("Excluir");acoes.add(salvar);acoes.add(limpar);acoes.add(excluir);acoes.add(voltar());
        salvar.addActionListener(e->salvar());limpar.addActionListener(e->limpar());excluir.addActionListener(e->excluir());tabela.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selecionar();});
        JPanel norte=new JPanel(new BorderLayout());norte.add(form,BorderLayout.CENTER);norte.add(acoes,BorderLayout.SOUTH);add(norte,BorderLayout.NORTH);add(new JScrollPane(tabela),BorderLayout.CENTER);pack();
    }
    private void atualizar(){modelo.setRowCount(0);if(cliente)for(Cliente p:controller.clientes(""))modelo.addRow(new Object[]{p.id(),p.nome(),p.cpf()});else for(Vendedor p:controller.vendedores())modelo.addRow(new Object[]{p.id(),p.nome(),p.cpf()});}
    private void selecionar(){int linha=tabela.getSelectedRow();if(linha<0)return;selecionado=((Number)modelo.getValueAt(linha,0)).longValue();nome.setText((String)modelo.getValueAt(linha,1));cpf.setText((String)modelo.getValueAt(linha,2));}
    private void salvar(){try{if(cliente)controller.salvarCliente(selecionado,nome.getText(),cpf.getText());else controller.salvarVendedor(selecionado,nome.getText(),cpf.getText());sucesso(selecionado==null?"Cadastro realizado.":"Cadastro atualizado.");limpar();atualizar();}catch(Exception e){erro(e);}}
    private void excluir(){if(selecionado==null){erro(new IllegalArgumentException("Selecione um registro."));return;}if(JOptionPane.showConfirmDialog(this,"Excluir o registro selecionado?","Confirmar",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;try{if(cliente)controller.excluirCliente(selecionado);else controller.excluirVendedor(selecionado);limpar();atualizar();sucesso("Registro excluído.");}catch(Exception e){erro(e);}}
    private void limpar(){selecionado=null;nome.setText("");cpf.setText("");tabela.clearSelection();}
}
