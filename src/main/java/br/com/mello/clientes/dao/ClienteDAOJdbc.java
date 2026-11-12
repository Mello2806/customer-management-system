package br.com.mello.clientes.dao;

import br.com.mello.clientes.config.ConnectionFactory;
import br.com.mello.clientes.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do ClienteDAO usando JDBC puro com PostgreSQL.
 *
 * Sempre usamos PreparedStatement com "?" em vez de concatenar strings no SQL:
 * isso evita SQL Injection.
 */
public class ClienteDAOJdbc implements ClienteDAO {

    private static final String COLUNAS = "id, nome, email, cpf, telefone, data_cadastro";

    private final ConnectionFactory connectionFactory;

    public ClienteDAOJdbc(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, email, cpf, telefone) VALUES (?, ?, ?, ?) "
                + "RETURNING id, data_cadastro";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                cliente.setId(rs.getInt("id"));
                cliente.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
            }
            return cliente;
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return buscarUm("SELECT " + COLUNAS + " FROM clientes WHERE id = ?", id);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return buscarUm("SELECT " + COLUNAS + " FROM clientes WHERE LOWER(email) = LOWER(?)", email);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return buscarUm("SELECT " + COLUNAS + " FROM clientes WHERE cpf = ?", cpf);
    }

    @Override
    public List<Cliente> listarTodos() {
        return buscarVarios("SELECT " + COLUNAS + " FROM clientes ORDER BY nome");
    }

    @Override
    public List<Cliente> buscarPorNome(String trecho) {
        return buscarVarios("SELECT " + COLUNAS + " FROM clientes WHERE nome ILIKE ? ORDER BY nome",
                "%" + trecho + "%");
    }

    @Override
    public boolean atualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, email = ?, cpf = ?, telefone = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setInt(5, cliente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar cliente #" + cliente.getId(), e);
        }
    }

    @Override
    public boolean excluir(int id) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM clientes WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir cliente #" + id, e);
        }
    }

    private Optional<Cliente> buscarUm(String sql, Object parametro) {
        List<Cliente> resultado = buscarVarios(sql, parametro);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    private List<Cliente> buscarVarios(String sql, Object... parametros) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            List<Cliente> clientes = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(converter(rs));
                }
            }
            return clientes;
        } catch (SQLException e) {
            throw new DAOException("Erro ao consultar clientes", e);
        }
    }

    private Cliente converter(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setEmail(rs.getString("email"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setTelefone(rs.getString("telefone"));
        Timestamp data = rs.getTimestamp("data_cadastro");
        cliente.setDataCadastro(data == null ? null : data.toLocalDateTime());
        return cliente;
    }
}
