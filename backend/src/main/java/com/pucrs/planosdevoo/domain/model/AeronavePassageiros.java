package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;

public class AeronavePassageiros extends Aeronave {

    private static final int ALTITUDE_MINIMA = 28_000;

    private final String companhiaAerea;
    private final int capacidadePassageiros;

    public AeronavePassageiros(String prefixo, double velocidadeCruzeiroKmh, double autonomiaKm,
                               String companhiaAerea, int capacidadePassageiros) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        if (companhiaAerea == null || companhiaAerea.isBlank()) {
            throw new IllegalArgumentException("companhiaAerea é obrigatório para aeronave PASSAGEIROS");
        }
        if (capacidadePassageiros <= 0) {
            throw new IllegalArgumentException("capacidadePassageiros deve ser positiva");
        }
        this.companhiaAerea = companhiaAerea;
        this.capacidadePassageiros = capacidadePassageiros;
    }

    @Override
    public boolean podeOperar(int altitude, LocalDateTime entrada, LocalDateTime saida) {
        return altitude >= ALTITUDE_MINIMA;
    }

    @Override
    public String getCategoria() { return "PASSAGEIROS"; }

    @Override
    public DadosAeronave dados() {
        return dadosComuns(null, companhiaAerea, capacidadePassageiros, null);
    }

    public String getCompanhiaAerea() { return companhiaAerea; }
    public int getCapacidadePassageiros() { return capacidadePassageiros; }
}
