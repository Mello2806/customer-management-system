package br.com.mello.clientes.dao;

import br.com.mello.clientes.model.Cliente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO falso que guarda os clientes num Map. Usado para testar o ClienteService
 * sem precisar de um PostgreSQL rodando.
 */
public class ClienteDAOEmMemoria implements ClienteDAO {

    private final Map<Integer, Cliente> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public Cliente inserir(Cliente cliente) {
        cliente.setId(proximoId++);
        cliente.setDataCadastro(LocalDateTime.now());
        dados.put(cliente.getId(), copiar(cliente));
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return Optional.ofNullable(dados.get(id)).map(this::copiar);
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        dados.values().forEach(c -> lista.add(copiar(c)));
        lista.sort(Comparator.comparing(Cliente::getNome));
        return lista;
    }

    @Override
    public List<Cliente> buscarPorNome(String trecho) {
        return listarTodos().stream()
                .filter(c -> c.getNome().toLowerCase().contains(trecho.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return dados.values().stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst().map(this::copiar);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return dados.values().stream().filter(c -> c.getCpf().equals(cpf)).findFirst().map(this::copiar);
    }

    @Override
    public boolean atualizar(Cliente cliente) {
        if (!dados.containsKey(cliente.getId())) {
            return false;
        }
        dados.put(cliente.getId(), copiar(cliente));
        return true;
    }

    @Override
    public boolean excluir(int id) {
        return dados.remove(id) != null;
    }

    /** Devolve cópias, como um banco de verdade faria (alterar o objeto não altera o "banco"). */
    private Cliente copiar(Cliente original) {
        Cliente c = new Cliente(original.getNome(), original.getEmail(), original.getCpf(), original.getTelefone());
        c.setId(original.getId());
        c.setDataCadastro(original.getDataCadastro());
        return c;
    }
}
