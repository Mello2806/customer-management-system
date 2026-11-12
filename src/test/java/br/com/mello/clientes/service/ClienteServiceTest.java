package br.com.mello.clientes.service;

import br.com.mello.clientes.dao.ClienteDAOEmMemoria;
import br.com.mello.clientes.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteServiceTest {

    private ClienteService service;

    @BeforeEach
    void setUp() {
        service = new ClienteService(new ClienteDAOEmMemoria());
    }

    private Cliente ana() {
        return new Cliente("  Ana   Souza ", "Ana@Email.com ", "529.982.247-25", "(81) 99999-0000");
    }

    @Test
    void deveCadastrarNormalizandoOsDados() {
        Cliente salvo = service.cadastrar(ana());

        assertNotNull(salvo.getId());
        assertEquals("Ana Souza", salvo.getNome());
        assertEquals("ana@email.com", salvo.getEmail());
        assertEquals("52998224725", salvo.getCpf());
    }

    @Test
    void telefoneEmBrancoViraNulo() {
        Cliente cliente = ana();
        cliente.setTelefone("   ");

        assertNull(service.cadastrar(cliente).getTelefone());
    }

    @Test
    void deveRecusarNomeCurto() {
        Cliente cliente = ana();
        cliente.setNome("Al");

        ValidacaoException erro = assertThrows(ValidacaoException.class, () -> service.cadastrar(cliente));
        assertTrue(erro.getMessage().contains("nome"));
    }

    @Test
    void deveRecusarEmailInvalido() {
        Cliente cliente = ana();
        cliente.setEmail("ana.email.com");

        assertThrows(ValidacaoException.class, () -> service.cadastrar(cliente));
    }

    @Test
    void deveRecusarCpfInvalido() {
        Cliente cliente = ana();
        cliente.setCpf("123.456.789-00");

        assertThrows(ValidacaoException.class, () -> service.cadastrar(cliente));
    }

    @Test
    void deveRecusarEmailDuplicado() {
        service.cadastrar(ana());
        Cliente outro = new Cliente("Outra Pessoa", "ANA@email.com", "111.444.777-35", null);

        ValidacaoException erro = assertThrows(ValidacaoException.class, () -> service.cadastrar(outro));
        assertTrue(erro.getMessage().contains("e-mail"));
    }

    @Test
    void deveRecusarCpfDuplicado() {
        service.cadastrar(ana());
        Cliente outro = new Cliente("Outra Pessoa", "outra@email.com", "52998224725", null);

        ValidacaoException erro = assertThrows(ValidacaoException.class, () -> service.cadastrar(outro));
        assertTrue(erro.getMessage().contains("CPF"));
    }

    @Test
    void deveAtualizarMantendoOProprioEmailECpf() {
        Cliente salvo = service.cadastrar(ana());
        salvo.setNome("Ana Souza Lima");

        service.atualizar(salvo);

        assertEquals("Ana Souza Lima", service.buscarPorId(salvo.getId()).getNome());
    }

    @Test
    void deveBuscarPorParteDoNome() {
        service.cadastrar(ana());
        service.cadastrar(new Cliente("Bruno Lima", "bruno@email.com", "111.444.777-35", null));

        assertEquals(1, service.buscarPorNome("brun").size());
        assertEquals(2, service.buscarPorNome("").size());
    }

    @Test
    void deveExcluirCliente() {
        Cliente salvo = service.cadastrar(ana());

        service.excluir(salvo.getId());

        assertTrue(service.listarTodos().isEmpty());
        assertThrows(ValidacaoException.class, () -> service.buscarPorId(salvo.getId()));
    }

    @Test
    void deveAvisarQuandoIdNaoExiste() {
        assertThrows(ValidacaoException.class, () -> service.excluir(99));
        assertThrows(ValidacaoException.class, () -> service.buscarPorId(99));
    }
}
