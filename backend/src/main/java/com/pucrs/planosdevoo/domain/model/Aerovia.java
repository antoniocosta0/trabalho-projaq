package com.pucrs.planosdevoo.domain.model;

import java.time.Duration;

public class Aerovia {

    private final String id;
    private final String origem;
    private final String destino;
    private final double distanciaKm;

    public Aerovia(String id, String origem, String destino, double distanciaKm) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador da aerovia é obrigatório");
        }
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Aerovia precisa de origem e destino");
        }
        if (origem.equals(destino)) {
            throw new IllegalArgumentException("Aerovia precisa ligar dois aeroportos diferentes: "
                    + origem + " foi informado como origem e destino");
        }
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("Distância da aerovia deve ser positiva");
        }
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    // arredondada ao minuto para a entrada informada poder bater com a chegada calculada
    public Duration duracao(double velocidadeKmh) {
        long minutos = Math.round(distanciaKm / velocidadeKmh * 60);
        return Duration.ofMinutes(minutos);
    }

    public String getId() { return id; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
