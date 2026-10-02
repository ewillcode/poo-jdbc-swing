package br.edu.materialconstrucao.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.materialconstrucao.model.Cliente;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClienteControllerTest {

    @TempDir
    Path pasta;

    private ClienteController clientes;

    @BeforeEach
    void preparar() {
        clientes = new BancoDeTeste(pasta).clientes;
    }

    @Test
    void salvaCpfSomenteComDigitos() {
        Cliente ana = clientes.salvar(null, "  Ana Souza ", "529.982.247-25");

        assertEquals("Ana Souza", ana.nome());
        assertEquals("52998224725", ana.cpf());
    }

    @Test
    void rejeitaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> clientes.salvar(null, "Ana", "123"));
        assertThrows(IllegalArgumentException.class, () -> clientes.salvar(null, "Ana", "111.111.111-11"));
        assertThrows(IllegalArgumentException.class, () -> clientes.salvar(null, "Ana", "529.982.247-26"));
    }

    @Test
    void rejeitaNomeVazio() {
        assertThrows(IllegalArgumentException.class, () -> clientes.salvar(null, "   ", "52998224725"));
    }

    @Test
    void rejeitaCpfDuplicado() {
        clientes.salvar(null, "Ana", "52998224725");

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> clientes.salvar(null, "Outra Ana", "529.982.247-25"));
        assertEquals("CPF já cadastrado.", erro.getMessage());
    }

    @Test
    void atualizaClienteExistente() {
        Cliente ana = clientes.salvar(null, "Ana", "52998224725");

        clientes.salvar(ana.id(), "Ana Souza", "52998224725");

        assertEquals("Ana Souza", clientes.listar().getFirst().nome());
    }

    @Test
    void buscaPorNomeOuPorCpfComPontuacao() {
        clientes.salvar(null, "Ana Souza", "52998224725");
        clientes.salvar(null, "Carlos Lima", "11144477735");

        assertEquals(List.of("Ana Souza"), nomes(clientes.buscar("ana")));
        assertEquals(List.of("Carlos Lima"), nomes(clientes.buscar("111.444")));
        assertEquals(2, clientes.buscar("").size());
    }

    private static List<String> nomes(List<Cliente> lista) {
        return lista.stream().map(Cliente::nome).toList();
    }
}
