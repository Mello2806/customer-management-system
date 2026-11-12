package br.com.mello.clientes.dao;

import br.com.mello.clientes.config.ConnectionFactory;
import br.com.mello.clientes.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Teste de integração: roda contra o PostgreSQL de verdade.
 *
 * Só executa se o banco estiver configurado (db.properties ou variáveis DB_*);
 * caso contrário é pulado. ATENÇÃO: apaga todos os dados da tabela clientes,
 * então use um banco de testes.
 */
class ClienteDAOJdbcTest {

    private ClienteDAOJdbc dao;

    @BeforeEach
    void setUp() throws Exception {
        ConnectionFactory factory;
        try {
            factory = ConnectionFactory.doAmbiente();
            try (Connection conn = factory.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE clientes RESTART IDENTITY");
            }
        } catch (Exception e) {
            assumeTrue(false, "Banco não configurado, teste de integração pulado: " + e.getMessage());
            return;
        }
        dao = new ClienteDAOJdbc(factory);
    }

    @Test
    void deveFazerOCrudCompleto() {
        Cliente cliente = dao.inserir(new Cliente("Ana Souza", "ana@email.com", "52998224725", null));
        assertNotNull(cliente.getId());
        assertNotNull(cliente.getDataCadastro());

        assertEquals("Ana Souza", dao.buscarPorId(cliente.getId()).orElseThrow().getNome());
        assertTrue(dao.buscarPorEmail("ANA@email.com").isPresent());
        assertTrue(dao.buscarPorCpf("52998224725").isPresent());
        assertEquals(1, dao.buscarPorNome("sou").size());

        cliente.setTelefone("81999990000");
        assertTrue(dao.atualizar(cliente));
        assertEquals("81999990000", dao.buscarPorId(cliente.getId()).orElseThrow().getTelefone());

        assertTrue(dao.excluir(cliente.getId()));
        assertTrue(dao.listarTodos().isEmpty());
        assertFalse(dao.excluir(cliente.getId()));
    }

    @Test
    void bancoDeveImpedirEmailDuplicado() {
        dao.inserir(new Cliente("Ana Souza", "ana@email.com", "52998224725", null));

        assertThrows(DAOException.class,
                () -> dao.inserir(new Cliente("Outra", "ana@email.com", "11144477735", null)));
    }
}
