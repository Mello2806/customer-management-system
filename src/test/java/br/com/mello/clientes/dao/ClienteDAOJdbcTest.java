package br.com.mello.clientes.dao;

import br.com.mello.clientes.config.ConnectionFactory;
import br.com.mello.clientes.model.Cliente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Teste de integração: roda contra o PostgreSQL de verdade.
 *
 * Só executa se o banco estiver configurado (db.properties ou variáveis DB_*);
 * caso contrário é pulado. Cada teste usa e-mails e CPFs únicos e apaga, no final,
 * apenas os clientes que ele mesmo criou. Os seus cadastros não são tocados.
 */
class ClienteDAOJdbcTest {

    private ClienteDAOJdbc dao;
    private final List<Integer> criados = new ArrayList<>();
    private final String sufixo = Long.toString(System.nanoTime());

    @BeforeEach
    void setUp() {
        ConnectionFactory factory;
        try {
            factory = ConnectionFactory.doAmbiente();
            try (Connection ignored = factory.getConnection()) {
                // só confere se o banco responde
            }
        } catch (Exception e) {
            assumeTrue(false, "Banco não configurado, teste de integração pulado: " + e.getMessage());
            return;
        }
        dao = new ClienteDAOJdbc(factory);
    }

    @AfterEach
    void limpar() {
        if (dao != null) {
            criados.forEach(dao::excluir);
        }
    }

    private Cliente inserir(String nome, String email) {
        Cliente cliente = dao.inserir(new Cliente(nome, email, cpfAleatorio(), null));
        criados.add(cliente.getId());
        return cliente;
    }

    /** O DAO não valida CPF (quem valida é o service), então 11 dígitos quaisquer servem. */
    private static String cpfAleatorio() {
        return String.format("%011d", ThreadLocalRandom.current().nextLong(1L, 99_999_999_999L));
    }

    @Test
    void deveFazerOCrudCompleto() {
        String email = "teste-" + sufixo + "@exemplo.com";
        Cliente cliente = inserir("Teste Integracao " + sufixo, email);
        assertNotNull(cliente.getId());
        assertNotNull(cliente.getDataCadastro());

        assertEquals("Teste Integracao " + sufixo, dao.buscarPorId(cliente.getId()).orElseThrow().getNome());
        assertTrue(dao.buscarPorEmail(email.toUpperCase()).isPresent());
        assertTrue(dao.buscarPorCpf(cliente.getCpf()).isPresent());
        assertEquals(1, dao.buscarPorNome("integracao " + sufixo).size());

        cliente.setTelefone("81999990000");
        assertTrue(dao.atualizar(cliente));
        assertEquals("81999990000", dao.buscarPorId(cliente.getId()).orElseThrow().getTelefone());

        assertTrue(dao.excluir(cliente.getId()));
        assertTrue(dao.buscarPorId(cliente.getId()).isEmpty());
        assertFalse(dao.excluir(cliente.getId()));
    }

    @Test
    void bancoDeveImpedirEmailDuplicado() {
        String email = "duplicado-" + sufixo + "@exemplo.com";
        inserir("Primeiro Teste", email);

        assertThrows(DAOException.class, () -> inserir("Segundo Teste", email));
    }
}
