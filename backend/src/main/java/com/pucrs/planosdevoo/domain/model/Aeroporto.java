package com.pucrs.planosdevoo.domain.model;

import java.util.List;

public class Aeroporto {

    private final String codigo;
    private final List<Integer> vagas;

    public Aeroporto(String codigo, List<Integer> vagas) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do aeroporto é obrigatório");
        }
        this.codigo = codigo;
        this.vagas = List.copyOf(vagas);
    }

    public boolean possuiVaga(int numero) {
        return vagas.contains(numero);
    }

    public String getCodigo() { return codigo; }
    public List<Integer> getVagas() { return vagas; }
}
