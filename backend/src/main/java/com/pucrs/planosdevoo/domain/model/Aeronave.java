package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;

public abstract sealed class Aeronave permits AeronaveParticular, AeronavePassageiros, AeronaveCarga {

    // §3: margem de segurança de 15%
    private static final double FRACAO_AUTONOMIA_UTILIZAVEL = 0.85;

    private final String prefixo;
    private final double velocidadeCruzeiroKmh;
    private final double autonomiaKm;

    protected Aeronave(String prefixo, double velocidadeCruzeiroKmh, double autonomiaKm) {
        if (prefixo == null || prefixo.isBlank()) {
            throw new IllegalArgumentException("Prefixo da aeronave é obrigatório");
        }
        if (velocidadeCruzeiroKmh <= 0 || autonomiaKm <= 0) {
            throw new IllegalArgumentException("Velocidade e autonomia devem ser positivas");
        }
        this.prefixo = prefixo;
        this.velocidadeCruzeiroKmh = velocidadeCruzeiroKmh;
        this.autonomiaKm = autonomiaKm;
    }

    public double distanciaMaxSemAbastecer() {
        return autonomiaKm * FRACAO_AUTONOMIA_UTILIZAVEL;
    }

    public abstract boolean podeOperar(int altitude, LocalDateTime entrada, LocalDateTime saida);

    public abstract String getCategoria();

    public String getPrefixo() { return prefixo; }
    public double getVelocidadeCruzeiroKmh() { return velocidadeCruzeiroKmh; }
    public double getAutonomiaKm() { return autonomiaKm; }
}
