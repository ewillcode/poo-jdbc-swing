package br.edu.materialconstrucao.view;

import br.edu.materialconstrucao.dao.Database;
import javax.swing.*;
import java.awt.*;

abstract class BaseFrame extends JFrame {
    protected final Database db; protected final HubFrame hub;
    BaseFrame(String titulo, Database db, HubFrame hub) {
        super(titulo); this.db=db; this.hub=hub; setDefaultCloseOperation(DISPOSE_ON_CLOSE); setMinimumSize(new Dimension(760,500)); setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){hub.setVisible(true);}});
    }
    protected JButton voltar(){ JButton b=new JButton("Voltar ao Hub"); b.addActionListener(e -> dispose()); return b; }
    protected void erro(Exception e){ JOptionPane.showMessageDialog(this,e.getMessage(),"Atenção",JOptionPane.WARNING_MESSAGE); }
    protected void sucesso(String mensagem){ JOptionPane.showMessageDialog(this,mensagem,"Sistema de Material de Construção",JOptionPane.INFORMATION_MESSAGE); }
    protected static String dinheiro(java.math.BigDecimal valor){return "R$ " + valor.setScale(2).toPlainString().replace('.',',');}
}
