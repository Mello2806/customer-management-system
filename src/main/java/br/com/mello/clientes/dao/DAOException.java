package br.com.mello.clientes.dao;

/**
 * Erro ao acessar o banco de dados. Envolve a SQLException para que as camadas
 * de cima não precisem lidar com detalhes do JDBC.
 */
public class DAOException extends RuntimeException {

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
