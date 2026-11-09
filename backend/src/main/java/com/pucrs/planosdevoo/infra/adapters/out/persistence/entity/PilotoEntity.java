package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "piloto")
public class PilotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    protected PilotoEntity() { }

    public PilotoEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
}
