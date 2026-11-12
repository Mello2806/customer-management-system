package br.com.mello.clientes.service;

/**
 * Validação de CPF pelos dígitos verificadores.
 *
 * Como funciona: os 9 primeiros dígitos são multiplicados por pesos decrescentes
 * (10, 9, ..., 2); o resto da soma por 11 define o 1º dígito verificador.
 * O 2º dígito é calculado igual, com os 10 primeiros dígitos e pesos de 11 a 2.
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    /** Remove pontos, traços e espaços: "123.456.789-09" vira "12345678909". */
    public static String somenteDigitos(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    public static boolean isValido(String cpf) {
        String digitos = somenteDigitos(cpf);
        if (digitos.length() != 11) {
            return false;
        }
        // CPFs com todos os dígitos iguais (111.111.111-11) passam na conta, mas são inválidos.
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        int primeiro = calcularDigito(digitos, 9);
        int segundo = calcularDigito(digitos, 10);
        return primeiro == digitos.charAt(9) - '0' && segundo == digitos.charAt(10) - '0';
    }

    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        int peso = quantidade + 1;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
