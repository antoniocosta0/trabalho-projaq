package com.pucrs.cadastro.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aerovia")
public class AeroviaEntity {

    @Id
    private String id;

    @Column(name = "origem_codigo", nullable = false)
    private String origem;

    @Column(name = "destino_codigo", nullable = false)
    private String destino;

    @Column(name = "distancia_km", nullable = false)
    private double distanciaKm;

    protected AeroviaEntity() { }

    public AeroviaEntity(String id, String origem, String destino, double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public String getId() { return id; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
