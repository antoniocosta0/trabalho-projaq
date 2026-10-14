package com.pucrs.planosdevoo.domain.model;

public class Aeroporto {

    private final String codigo;
    private final String nome;
    private final int quantidadeVagas;

    public Aeroporto(String codigo, String nome, int quantidadeVagas) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do aeroporto é obrigatório");
        }
        if (quantidadeVagas < 0) {
            throw new IllegalArgumentException("Quantidade de vagas não pode ser negativa");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeVagas = quantidadeVagas;
    }

    // As vagas são numeradas de 1 até a quantidade cadastrada.
    public boolean possuiVaga(int numero) {
        return numero >= 1 && numero <= quantidadeVagas;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getQuantidadeVagas() { return quantidadeVagas; }
}
