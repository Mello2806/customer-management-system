package br.com.mello.clientes.service;

/**
 * Dados do cliente inválidos ou regra de negócio violada.
 * A mensagem é pensada para ser mostrada ao usuário.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
