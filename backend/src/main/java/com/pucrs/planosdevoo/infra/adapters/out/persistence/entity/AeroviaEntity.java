package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroportoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aerovia")
public class AeroviaEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_codigo")
    private AeroportoEntity origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_codigo")
    private AeroportoEntity destino;

    @Column(name = "distancia_km", nullable = false)
    private double distanciaKm;

    protected AeroviaEntity() { }

    public AeroviaEntity(String id, AeroportoEntity origem, AeroportoEntity destino, double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public String getId() { return id; }
    public AeroportoEntity getOrigem() { return origem; }
    public AeroportoEntity getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
