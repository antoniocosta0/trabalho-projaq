package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

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

    public static List<Integer> altitudesPermitidas() {
        return IntStream.rangeClosed(ALTITUDE_MINIMA / INCREMENTO_ALTITUDE, ALTITUDE_MAXIMA / INCREMENTO_ALTITUDE)
                .map(milhares -> milhares * INCREMENTO_ALTITUDE)
                .boxed()
                .toList();
    }

    public List<Slot> slotsOcupados() {
        return Slot.entre(horarioEntrada, horarioChegada());
    }

    public List<Slot> slotsDaVaga() {
        if (pouso == null) {
            return List.of();
        }
        LocalDateTime chegada = horarioChegada();
        return Slot.entre(chegada, pouso.saida(chegada));
    }

    public List<ReservaAerovia> reservasDeAerovia(Long planoId) {
        return slotsOcupados().stream()
                .map(slot -> new ReservaAerovia(planoId, aerovia.getId(), altitude, slot))
                .toList();
    }

    public List<ReservaVaga> reservasDeVaga(Long planoId) {
        return slotsDaVaga().stream()
                .map(slot -> new ReservaVaga(planoId, aerovia.getDestino(), pouso.getNumeroVaga(), slot))
                .toList();
    }

    public int getOrdem() { return ordem; }
    public Aerovia getAerovia() { return aerovia; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getHorarioEntrada() { return horarioEntrada; }
    public Pouso getPouso() { return pouso; }
}
