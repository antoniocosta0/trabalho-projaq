package com.pucrs.cadastro.infra.adapters.out.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aeroporto")
public class AeroportoEntity {

    @Id
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vaga", joinColumns = @JoinColumn(name = "aeroporto_codigo"))
    @Column(name = "numero")
    @OrderBy
    private List<Integer> vagas = new ArrayList<>();

    protected AeroportoEntity() { }

    public AeroportoEntity(String codigo, String nome, List<Integer> vagas) {
        this.codigo = codigo;
        this.nome = nome;
        this.vagas = new ArrayList<>(vagas);
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public List<Integer> getVagas() { return vagas; }
}
