package br.com.mello.clientes.model;

import java.time.LocalDateTime;

/**
 * Representa um cliente cadastrado. Espelha a tabela "clientes" do banco.
 */
public class Cliente {

    private Integer id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private LocalDateTime dataCadastro;

    public Cliente() {
    }

    public Cliente(String nome, String email, String cpf, String telefone) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /** CPF no formato 000.000.000-00, para exibição. */
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | CPF %s | Tel. %s",
                id, nome, email, getCpfFormatado(), telefone == null ? "-" : telefone);
    }
}
