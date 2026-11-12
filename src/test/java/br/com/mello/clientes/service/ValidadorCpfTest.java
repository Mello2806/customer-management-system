package br.com.mello.clientes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorCpfTest {

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", "111.444.777-35"})
    void deveAceitarCpfsValidos(String cpf) {
        assertTrue(ValidadorCpf.isValido(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-24", "111.111.111-11", "123", "abc.def.ghi-jk", "529982247251"})
    void deveRecusarCpfsInvalidos(String cpf) {
        assertFalse(ValidadorCpf.isValido(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deveRecusarCpfVazio(String cpf) {
        assertFalse(ValidadorCpf.isValido(cpf));
    }

    @Test
    void deveRemoverPontuacao() {
        assertEquals("52998224725", ValidadorCpf.somenteDigitos(" 529.982.247-25 "));
    }
}
