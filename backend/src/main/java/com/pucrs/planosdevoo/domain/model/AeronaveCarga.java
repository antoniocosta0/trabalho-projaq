package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AeronaveCarga extends Aeronave {

    private static final LocalTime FIM_DA_JANELA = LocalTime.of(6, 0);

    private final String companhiaAerea;
    private final double capacidadeToneladas;

    public AeronaveCarga(String prefixo, double velocidadeCruzeiroKmh, double autonomiaKm,
                         String companhiaAerea, double capacidadeToneladas) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        this.companhiaAerea = companhiaAerea;
        this.capacidadeToneladas = capacidadeToneladas;
    }

    // Todo o uso da aerovia precisa caber entre 00:00 (inclusive) e 06:00
    // (exclusive) do mesmo dia. Uso que termina exatamente às 06:00 é aceito.
    @Override
    public boolean podeOperar(int altitude, LocalDateTime entrada, LocalDateTime saida) {
        boolean mesmoDia = entrada.toLocalDate().equals(saida.toLocalDate());
        return mesmoDia && entrada.toLocalTime().isBefore(FIM_DA_JANELA)
                && !saida.toLocalTime().isAfter(FIM_DA_JANELA);
    }

    @Override
    public String getCategoria() { return "CARGA"; }

    public String getCompanhiaAerea() { return companhiaAerea; }
    public double getCapacidadeToneladas() { return capacidadeToneladas; }
}
