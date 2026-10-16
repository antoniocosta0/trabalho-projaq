package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Trecho {

    private static final int ALTITUDE_MINIMA = 25_000;
    private static final int ALTITUDE_MAXIMA = 35_000;
    private static final int INCREMENTO_ALTITUDE = 1_000;

    private final int ordem;
    private final Aerovia aerovia;
    private final int altitude;
    private final LocalDateTime horarioEntrada;
    private final double velocidadeCruzeiroKmh;
    private final Pouso pouso;

    public Trecho(int ordem, Aerovia aerovia, int altitude, LocalDateTime horarioEntrada,
                  double velocidadeCruzeiroKmh, Pouso pouso) {
        if (ordem < 1) {
            throw new IllegalArgumentException("Ordem do trecho deve ser positiva");
        }
        if (aerovia == null) {
            throw new IllegalArgumentException("Aerovia é obrigatória");
        }
        if (altitude < ALTITUDE_MINIMA || altitude > ALTITUDE_MAXIMA
                || altitude % INCREMENTO_ALTITUDE != 0) {
            throw new IllegalArgumentException("Altitude deve estar entre 25000 e 35000 pés, de 1000 em 1000");
        }
        if (horarioEntrada == null) {
            throw new IllegalArgumentException("Horário de entrada é obrigatório");
        }
        if (velocidadeCruzeiroKmh <= 0) {
            throw new IllegalArgumentException("Velocidade de cruzeiro deve ser positiva");
        }
        this.ordem = ordem;
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.horarioEntrada = horarioEntrada;
        this.velocidadeCruzeiroKmh = velocidadeCruzeiroKmh;
        this.pouso = pouso;
    }

    public LocalDateTime horarioChegada() {
        return horarioEntrada.plus(aerovia.duracao(velocidadeCruzeiroKmh));
    }

    public List<Slot> slotsOcupados() {
        return Slot.entre(horarioEntrada, horarioChegada());
    }

    public int getOrdem() { return ordem; }
    public Aerovia getAerovia() { return aerovia; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getHorarioEntrada() { return horarioEntrada; }
    public Pouso getPouso() { return pouso; }
}
