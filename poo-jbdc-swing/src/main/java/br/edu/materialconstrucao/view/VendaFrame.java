package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.controller.*;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;

final class VendaFrame extends BaseFrame {
    private final CadastroController cadastros; private final VendaController vendas;
    private final JComboBox<Cliente> clientes=new JComboBox<>();private final JComboBox<Vendedor> vendedores=new JComboBox<>();private final JComboBox<Produto> produtos=new JComboBox<>();private final JSpinner quantidade=new JSpinner(new SpinnerNumberModel(1,1,9999,1));private final JLabel total=new JLabel("Total: R$ 0,00");
    private final java.util.List<ItemVenda> carrinho=new ArrayList<>();private final DefaultTableModel modelo=new DefaultTableModel(new String[]{"Produto","Qtd.","Preço unit.","Subtotal"},0){public boolean isCellEditable(int r,int c){return false;}};private final JTable tabela=new JTable(modelo);
    VendaFrame(Database db,HubFrame hub){super("Cadastrar Venda",db,hub);cadastros=new CadastroController(db);vendas=new VendaController(db);montar();carregarCombos();}
    private void montar(){
        JPanel dados=new JPanel(new GridLayout(3,2,8,8));dados.setBorder(BorderFactory.createTitledBorder("Dados da venda"));dados.add(new JLabel("Cliente:"));dados.add(clientes);dados.add(new JLabel("Vendedor:"));dados.add(vendedores);dados.add(new JLabel("Produto / quantidade:"));JPanel item=new JPanel(new BorderLayout(8,0));item.add(produtos);item.add(quantidade,BorderLayout.EAST);dados.add(item);
        JButton incluir=new JButton("Incluir no carrinho"),remover=new JButton("Remover item"),finalizar=new JButton("Finalizar Venda");JPanel acoes=new JPanel();acoes.add(incluir);acoes.add(remover);acoes.add(finalizar);acoes.add(total);acoes.add(voltar());incluir.addActionListener(e->incluir());remover.addActionListener(e->remover());finalizar.addActionListener(e->finalizar());
        add(dados,BorderLayout.NORTH);add(new JScrollPane(tabela),BorderLayout.CENTER);add(acoes,BorderLayout.SOUTH);pack();
    }
    private void carregarCombos(){clientes.removeAllItems();vendedores.removeAllItems();produtos.removeAllItems();for(Cliente c:cadastros.clientes(""))clientes.addItem(c);for(Vendedor v:cadastros.vendedores())vendedores.addItem(v);for(Produto p:cadastros.produtos())produtos.addItem(p);}
    private void incluir(){try{ItemVenda novo=vendas.criarItem((Produto)produtos.getSelectedItem(),(Integer)quantidade.getValue());for(int i=0;i<carrinho.size();i++)if(carrinho.get(i).produto().id().equals(novo.produto().id())){int q=carrinho.get(i).quantidade()+novo.quantidade();carrinho.set(i,vendas.criarItem(novo.produto(),q));atualizarCarrinho();return;}carrinho.add(novo);atualizarCarrinho();}catch(Exception e){erro(e);}}
    private void remover(){int r=tabela.getSelectedRow();if(r<0){erro(new IllegalArgumentException("Selecione um item do carrinho."));return;}carrinho.remove(r);atualizarCarrinho();}
    private void atualizarCarrinho(){modelo.setRowCount(0);BigDecimal soma=BigDecimal.ZERO;for(ItemVenda i:carrinho){modelo.addRow(new Object[]{i.produto().nome(),i.quantidade(),dinheiro(i.precoUnitario()),dinheiro(i.subtotal())});soma=soma.add(i.subtotal());}total.setText("Total: "+dinheiro(soma));}
    private void finalizar(){try{Venda venda=vendas.finalizar((Cliente)clientes.getSelectedItem(),(Vendedor)vendedores.getSelectedItem(),carrinho);sucesso("Venda #"+venda.id()+" finalizada com sucesso.");carrinho.clear();atualizarCarrinho();carregarCombos();}catch(Exception e){erro(e);}}
}
