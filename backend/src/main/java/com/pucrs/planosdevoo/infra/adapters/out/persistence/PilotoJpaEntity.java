package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "piloto")
public class PilotoJpaEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    protected PilotoJpaEntity() { }

    public Long getId() { return id; }
    public String getNome() { return nome; }
}
