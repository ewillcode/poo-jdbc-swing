package br.edu.materialconstrucao;

import br.edu.materialconstrucao.controller.*;
import br.edu.materialconstrucao.dao.Database;
import br.edu.materialconstrucao.model.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ControllerIntegrationTest {
    private Path arquivo; private CadastroController cadastros; private VendaController vendas;

    @BeforeEach void preparar() throws Exception { arquivo=Files.createTempFile("material-construcao-", ".db"); Database db=new Database(arquivo);db.inicializar();cadastros=new CadastroController(db);vendas=new VendaController(db); }
    @AfterEach void limpar() throws Exception { Files.deleteIfExists(arquivo); }

    @Test void rejeitaCpfInvalidoEDuplicado() {
        assertThrows(IllegalArgumentException.class,()->cadastros.salvarCliente(null,"Ana","123"));
        cadastros.salvarCliente(null,"Ana","123.456.789-01");
        IllegalArgumentException erro=assertThrows(IllegalArgumentException.class,()->cadastros.salvarCliente(null,"Outra Ana","12345678901"));
        assertEquals("CPF já cadastrado.",erro.getMessage());
    }

    @Test void gravaVendaComItensEPreservaPrecoHistorico() {
        Cliente cliente=cadastros.salvarCliente(null,"Ana","12345678901"); Vendedor vendedor=cadastros.salvarVendedor(null,"Bruno","98765432109");
        Produto cimento=cadastros.salvarProduto(null,"Cimento","20.00"); Produto areia=cadastros.salvarProduto(null,"Areia","5.50");
        Venda venda=vendas.finalizar(cliente,vendedor,List.of(vendas.criarItem(cimento,2),vendas.criarItem(areia,3)));
        assertEquals(0,new BigDecimal("56.50").compareTo(venda.total())); assertEquals(2,vendas.itens(venda.id()).size());
        cadastros.salvarProduto(cimento.id(),"Cimento","25.00");
        assertEquals(0,new BigDecimal("20.00").compareTo(vendas.itens(venda.id()).getFirst().precoUnitario()));
    }

    @Test void bloqueiaExclusaoDeDadosVinculadosAVenda() {
        Cliente cliente=cadastros.salvarCliente(null,"Ana","12345678901"); Vendedor vendedor=cadastros.salvarVendedor(null,"Bruno","98765432109");Produto produto=cadastros.salvarProduto(null,"Tijolo","2.00");
        vendas.finalizar(cliente,vendedor,List.of(vendas.criarItem(produto,1)));
        assertThrows(IllegalArgumentException.class,()->cadastros.excluirCliente(cliente.id()));
        assertThrows(IllegalArgumentException.class,()->cadastros.excluirVendedor(vendedor.id()));
        assertThrows(IllegalArgumentException.class,()->cadastros.excluirProduto(produto.id()));
    }
}
