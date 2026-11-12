package br.com.mello.clientes.dao;

import br.com.mello.clientes.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Operações de acesso a dados de clientes.
 *
 * Ser uma interface permite trocar a implementação: no programa usamos o JDBC
 * (ClienteDAOJdbc) e nos testes uma versão em memória, sem precisar de banco.
 */
public interface ClienteDAO {

    Cliente inserir(Cliente cliente);

    Optional<Cliente> buscarPorId(int id);

    List<Cliente> listarTodos();

    List<Cliente> buscarPorNome(String trecho);

    Optional<Cliente> buscarPorEmail(String email);

    Optional<Cliente> buscarPorCpf(String cpf);

    boolean atualizar(Cliente cliente);

    boolean excluir(int id);
}
