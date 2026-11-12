package br.com.mello.clientes;

import br.com.mello.clientes.config.ConnectionFactory;
import br.com.mello.clientes.dao.ClienteDAOJdbc;
import br.com.mello.clientes.dao.DAOException;
import br.com.mello.clientes.model.Cliente;
import br.com.mello.clientes.service.ClienteService;
import br.com.mello.clientes.service.ValidacaoException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu do sistema de cadastro de clientes no console.
 */
public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final ClienteService service;

    public Main(ClienteService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        ConnectionFactory factory;
        try {
            factory = ConnectionFactory.doAmbiente();
            try (Connection ignored = factory.getConnection()) {
                // só testa se o banco responde antes de abrir o menu
            }
        } catch (IllegalStateException | SQLException e) {
            System.err.println("Não foi possível conectar ao banco: " + e.getMessage());
            System.exit(1);
            return;
        }
        new Main(new ClienteService(new ClienteDAOJdbc(factory))).executar();
    }

    private void executar() {
        System.out.println("=== Cadastro de Clientes ===");
        boolean rodando = true;
        while (rodando) {
            mostrarMenu();
            String opcao = lerTexto("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case "1" -> cadastrar();
                    case "2" -> listar(service.listarTodos());
                    case "3" -> listar(service.buscarPorNome(lerTexto("Nome (ou parte): ")));
                    case "4" -> System.out.println(service.buscarPorId(lerId()));
                    case "5" -> editar();
                    case "6" -> excluir();
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (ValidacaoException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (DAOException e) {
                System.out.println("Erro no banco de dados: " + e.getCause().getMessage());
            }
        }
        System.out.println("Até mais!");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1 - Cadastrar cliente");
        System.out.println("2 - Listar clientes");
        System.out.println("3 - Buscar por nome");
        System.out.println("4 - Buscar por id");
        System.out.println("5 - Editar cliente");
        System.out.println("6 - Excluir cliente");
        System.out.println("0 - Sair");
    }

    private void cadastrar() {
        Cliente cliente = new Cliente(
                lerTexto("Nome: "),
                lerTexto("E-mail: "),
                lerTexto("CPF: "),
                lerTexto("Telefone (opcional): "));
        Cliente salvo = service.cadastrar(cliente);
        System.out.println("Cliente cadastrado: " + salvo);
    }

    private void listar(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado.");
            return;
        }
        clientes.forEach(System.out::println);
        System.out.println(clientes.size() + " cliente(s).");
    }

    private void editar() {
        Cliente cliente = service.buscarPorId(lerId());
        System.out.println("Editando: " + cliente);
        System.out.println("(deixe em branco para manter o valor atual)");
        cliente.setNome(lerOuManter("Nome", cliente.getNome()));
        cliente.setEmail(lerOuManter("E-mail", cliente.getEmail()));
        cliente.setCpf(lerOuManter("CPF", cliente.getCpf()));
        cliente.setTelefone(lerOuManter("Telefone", cliente.getTelefone()));
        System.out.println("Cliente atualizado: " + service.atualizar(cliente));
    }

    private void excluir() {
        Cliente cliente = service.buscarPorId(lerId());
        String confirmacao = lerTexto("Excluir " + cliente.getNome() + "? (s/n): ");
        if (confirmacao.equalsIgnoreCase("s")) {
            service.excluir(cliente.getId());
            System.out.println("Cliente excluído.");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    private String lerOuManter(String campo, String atual) {
        String novo = lerTexto(campo + " [" + (atual == null ? "" : atual) + "]: ");
        return novo.isEmpty() ? atual : novo;
    }

    private int lerId() {
        String texto = lerTexto("Id do cliente: ");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Id inválido: " + texto);
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Até mais!");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }
}
