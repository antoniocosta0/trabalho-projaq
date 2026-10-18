package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aerovia")
public class AeroviaJpaEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_codigo")
    private AeroportoJpaEntity origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_codigo")
    private AeroportoJpaEntity destino;

    @Column(name = "distancia_km", nullable = false)
    private double distanciaKm;

    protected AeroviaJpaEntity() { }

    // Só construtor, sem setter: aerovia não muda depois de cadastrada.
    public AeroviaJpaEntity(String id, AeroportoJpaEntity origem, AeroportoJpaEntity destino, double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public String getId() { return id; }
    public AeroportoJpaEntity getOrigem() { return origem; }
    public AeroportoJpaEntity getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
