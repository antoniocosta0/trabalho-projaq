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
        if (companhiaAerea == null || companhiaAerea.isBlank()) {
            throw new IllegalArgumentException("companhiaAerea é obrigatório para aeronave CARGA");
        }
        if (capacidadeToneladas <= 0) {
            throw new IllegalArgumentException("capacidadeToneladas deve ser positiva");
        }
        this.companhiaAerea = companhiaAerea;
        this.capacidadeToneladas = capacidadeToneladas;
    }

    // Todo o uso da aerovia precisa caber entre 00:00 (inclusive) e 06:00
    // (exclusive) do mesmo dia, como o §3 do enunciado: a saída às 06:00
    // exatamente já está fora da janela.
    @Override
    public boolean podeOperar(int altitude, LocalDateTime entrada, LocalDateTime saida) {
        boolean mesmoDia = entrada.toLocalDate().equals(saida.toLocalDate());
        return mesmoDia && entrada.toLocalTime().isBefore(FIM_DA_JANELA)
                && saida.toLocalTime().isBefore(FIM_DA_JANELA);
    }

    @Override
    public String getCategoria() { return "CARGA"; }

    @Override
    public DadosAeronave dados() {
        return dadosComuns(null, companhiaAerea, null, capacidadeToneladas);
    }

    public String getCompanhiaAerea() { return companhiaAerea; }
    public double getCapacidadeToneladas() { return capacidadeToneladas; }
}
