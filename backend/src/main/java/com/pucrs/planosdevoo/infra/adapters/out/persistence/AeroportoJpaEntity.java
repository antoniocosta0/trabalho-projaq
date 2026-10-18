package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aeroporto")
public class AeroportoJpaEntity {

    @Id
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(name = "quantidade_vagas", nullable = false)
    private int quantidadeVagas;

    protected AeroportoJpaEntity() { }

    public AeroportoJpaEntity(String codigo, String nome, int quantidadeVagas) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeVagas = quantidadeVagas;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getQuantidadeVagas() { return quantidadeVagas; }
}
