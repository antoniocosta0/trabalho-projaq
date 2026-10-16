package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Pouso {

    private final int numeroVaga;
    private final int permanenciaMinutos;

    public Pouso(int numeroVaga, int permanenciaMinutos) {
        if (numeroVaga < 1) {
            throw new IllegalArgumentException("Número da vaga deve ser positivo");
        }
        if (permanenciaMinutos < 1) {
            throw new IllegalArgumentException("Permanência deve ser positiva");
        }
        this.numeroVaga = numeroVaga;
        this.permanenciaMinutos = permanenciaMinutos;
    }

    public LocalDateTime saida(LocalDateTime chegada) {
        if (chegada == null) {
            throw new IllegalArgumentException("Chegada é obrigatória");
        }
        return chegada.plusMinutes(permanenciaMinutos);
    }

    public int getNumeroVaga() { return numeroVaga; }
    public int getPermanenciaMinutos() { return permanenciaMinutos; }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Pouso pouso)) {
            return false;
        }
        return numeroVaga == pouso.numeroVaga
                && permanenciaMinutos == pouso.permanenciaMinutos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroVaga, permanenciaMinutos);
    }
}
