package br.edu.materialconstrucao;

import br.edu.materialconstrucao.controller.ClienteController;
import br.edu.materialconstrucao.controller.ProdutoController;
import br.edu.materialconstrucao.controller.VendaController;
import br.edu.materialconstrucao.controller.VendedorController;
import br.edu.materialconstrucao.dao.ClienteDao;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.dao.ProdutoDao;
import br.edu.materialconstrucao.dao.VendaDao;
import br.edu.materialconstrucao.dao.VendedorDao;
import br.edu.materialconstrucao.view.HubFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada: prepara o banco, liga as camadas do MVC e abre o Hub.
 */
public final class Main {

    public static void main(String[] args) {
        Database banco = Database.padrao();
        banco.inicializar();

        // Model/DAO → Controller: cada controller recebe o DAO que vai usar
        ClienteController clientes = new ClienteController(new ClienteDao(banco));
        VendedorController vendedores = new VendedorController(new VendedorDao(banco));
        ProdutoController produtos = new ProdutoController(new ProdutoDao(banco));
        VendaController vendas = new VendaController(new VendaDao(banco));

        if (banco.estaVazio()) {
            new DadosExemplo(clientes, vendedores, produtos, vendas).inserir();
        }

        // Controller → View: as telas só conversam com os controllers
        SwingUtilities.invokeLater(() -> {
            usarVisualDoSistema();
            new HubFrame(clientes, vendedores, produtos, vendas).setVisible(true);
        });
    }

    private static void usarVisualDoSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // se não conseguir, o Swing usa o visual padrão dele
        }
    }
}
