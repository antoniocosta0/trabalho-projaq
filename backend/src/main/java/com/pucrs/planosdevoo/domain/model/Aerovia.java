package com.pucrs.planosdevoo.domain.model;

import java.time.Duration;

// Imutável: o enunciado proíbe alterar ou excluir uma aerovia depois do cadastro.
public class Aerovia {

    private final String id;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private final double distanciaKm;

    public Aerovia(String id, Aeroporto origem, Aeroporto destino, double distanciaKm) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador da aerovia é obrigatório");
        }
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Aerovia precisa de origem e destino");
        }
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("Distância da aerovia deve ser positiva");
        }
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    // duração = distância / velocidade de cruzeiro
    public Duration duracao(double velocidadeKmh) {
        long segundos = Math.round(distanciaKm / velocidadeKmh * 3600);
        return Duration.ofSeconds(segundos);
    }

    public String getId() { return id; }
    public Aeroporto getOrigem() { return origem; }
    public Aeroporto getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
