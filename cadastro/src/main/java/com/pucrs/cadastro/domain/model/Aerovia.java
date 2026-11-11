package com.pucrs.cadastro.domain.model;

public class Aerovia {

    private final String id;
    private final String origem;
    private final String destino;
    private final double distanciaKm;

    public Aerovia(String id, String origem, String destino, double distanciaKm) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador da aerovia é obrigatório");
        }
        if (origem.equals(destino)) {
            throw new IllegalArgumentException("Aerovia precisa ligar dois aeroportos diferentes");
        }
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("Distância da aerovia deve ser positiva");
        }
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
