package br.com.mello.clientes.service;

import br.com.mello.clientes.dao.ClienteDAO;
import br.com.mello.clientes.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regras de negócio do cadastro de clientes.
 *
 * O menu (Main) conversa só com esta classe; ela valida os dados e depois
 * chama o DAO para gravar no banco.
 */
public class ClienteService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final ClienteDAO dao;

    public ClienteService(ClienteDAO dao) {
        this.dao = dao;
    }

    public Cliente cadastrar(Cliente cliente) {
        normalizar(cliente);
        validarCampos(cliente);
        validarDuplicidade(cliente, null);
        return dao.inserir(cliente);
    }

    public Cliente atualizar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new ValidacaoException("Informe o id do cliente a ser atualizado.");
        }
        buscarPorId(cliente.getId());
        normalizar(cliente);
        validarCampos(cliente);
        validarDuplicidade(cliente, cliente.getId());
        dao.atualizar(cliente);
        return cliente;
    }

    public Cliente buscarPorId(int id) {
        return dao.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Cliente #" + id + " não encontrado."));
    }

    public List<Cliente> listarTodos() {
        return dao.listarTodos();
    }

    public List<Cliente> buscarPorNome(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return listarTodos();
        }
        return dao.buscarPorNome(trecho.trim());
    }

    public void excluir(int id) {
        if (!dao.excluir(id)) {
            throw new ValidacaoException("Cliente #" + id + " não encontrado.");
        }
    }

    private void normalizar(Cliente cliente) {
        cliente.setNome(cliente.getNome() == null ? null : cliente.getNome().trim().replaceAll("\\s+", " "));
        cliente.setEmail(cliente.getEmail() == null ? null : cliente.getEmail().trim().toLowerCase());
        cliente.setCpf(ValidadorCpf.somenteDigitos(cliente.getCpf()));
        String telefone = cliente.getTelefone() == null ? "" : cliente.getTelefone().trim();
        cliente.setTelefone(telefone.isEmpty() ? null : telefone);
    }

    private void validarCampos(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().length() < 3) {
            throw new ValidacaoException("O nome deve ter pelo menos 3 caracteres.");
        }
        if (cliente.getNome().length() > 100) {
            throw new ValidacaoException("O nome pode ter no máximo 100 caracteres.");
        }
        if (cliente.getEmail() == null || !EMAIL.matcher(cliente.getEmail()).matches()) {
            throw new ValidacaoException("E-mail inválido.");
        }
        if (!ValidadorCpf.isValido(cliente.getCpf())) {
            throw new ValidacaoException("CPF inválido.");
        }
        if (cliente.getTelefone() != null && cliente.getTelefone().replaceAll("\\D", "").length() < 10) {
            throw new ValidacaoException("Telefone inválido. Use DDD + número, ex: (81) 99999-9999.");
        }
    }

    /** Impede dois clientes com o mesmo e-mail ou CPF. idAtual é ignorado (para a edição). */
    private void validarDuplicidade(Cliente cliente, Integer idAtual) {
        Optional<Cliente> mesmoEmail = dao.buscarPorEmail(cliente.getEmail());
        if (mesmoEmail.isPresent() && !mesmoEmail.get().getId().equals(idAtual)) {
            throw new ValidacaoException("Já existe um cliente com este e-mail.");
        }
        Optional<Cliente> mesmoCpf = dao.buscarPorCpf(cliente.getCpf());
        if (mesmoCpf.isPresent() && !mesmoCpf.get().getId().equals(idAtual)) {
            throw new ValidacaoException("Já existe um cliente com este CPF.");
        }
    }
}
