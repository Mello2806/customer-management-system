package br.com.mello.clientes.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Cria conexões JDBC com o PostgreSQL.
 *
 * As configurações vêm, nesta ordem de prioridade:
 * 1. variáveis de ambiente DB_URL, DB_USUARIO e DB_SENHA;
 * 2. arquivo db.properties na pasta onde o programa foi iniciado.
 */
public class ConnectionFactory {

    private static final Path ARQUIVO_CONFIG = Path.of("db.properties");

    private final String url;
    private final String usuario;
    private final String senha;

    public ConnectionFactory(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    /** Lê a configuração das variáveis de ambiente ou do db.properties. */
    public static ConnectionFactory doAmbiente() {
        Properties arquivo = lerArquivo();
        String url = valor("DB_URL", arquivo, "db.url");
        String usuario = valor("DB_USUARIO", arquivo, "db.usuario");
        String senha = valor("DB_SENHA", arquivo, "db.senha");

        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException(
                    "Configuração do banco não encontrada. Copie db.properties.example para "
                            + "db.properties e preencha os dados, ou defina DB_URL, DB_USUARIO e DB_SENHA.");
        }
        return new ConnectionFactory(url, usuario, senha);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    private static Properties lerArquivo() {
        Properties props = new Properties();
        if (Files.exists(ARQUIVO_CONFIG)) {
            try (InputStream in = Files.newInputStream(ARQUIVO_CONFIG)) {
                props.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Erro ao ler " + ARQUIVO_CONFIG, e);
            }
        }
        return props;
    }

    private static String valor(String variavelAmbiente, Properties arquivo, String chave) {
        String doAmbiente = System.getenv(variavelAmbiente);
        if (doAmbiente != null && !doAmbiente.isBlank()) {
            return doAmbiente;
        }
        return arquivo.getProperty(chave);
    }
}
