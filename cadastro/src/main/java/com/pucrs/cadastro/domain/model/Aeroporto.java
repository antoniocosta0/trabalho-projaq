package com.pucrs.cadastro.domain.model;

import java.util.List;
import java.util.stream.IntStream;

public class Aeroporto {

    private final String codigo;
    private final String nome;
    private final List<Integer> vagas;

    public Aeroporto(String codigo, String nome, List<Integer> vagas) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do aeroporto é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do aeroporto é obrigatório");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.vagas = List.copyOf(vagas);
    }

    public static Aeroporto comVagas(String codigo, String nome, int quantidadeVagas) {
        if (quantidadeVagas < 0) {
            throw new IllegalArgumentException("Quantidade de vagas não pode ser negativa");
        }
        return new Aeroporto(codigo, nome, IntStream.rangeClosed(1, quantidadeVagas).boxed().toList());
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public List<Integer> getVagas() { return vagas; }
}
