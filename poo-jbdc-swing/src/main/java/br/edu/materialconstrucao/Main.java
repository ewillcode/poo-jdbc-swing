package br.edu.materialconstrucao;

import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.view.HubFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    public static void main(String[] args) {
        Database banco=Database.padrao(); banco.inicializar();
        SwingUtilities.invokeLater(() -> { try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {} new HubFrame(banco).setVisible(true); });
    }
}
