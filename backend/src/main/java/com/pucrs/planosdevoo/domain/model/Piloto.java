package com.pucrs.planosdevoo.domain.model;

public class Piloto {

    private final Long id;
    private final String nome;

    public Piloto(Long id, String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do piloto é obrigatório");
        }
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
}
