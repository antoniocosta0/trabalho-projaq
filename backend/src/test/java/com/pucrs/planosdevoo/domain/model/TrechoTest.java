package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrechoTest {

    @Test
    void calculaChegadaPelaDistanciaEVelocidade() {
        Trecho trecho = trecho(250, 200, LocalDateTime.of(2026, 10, 19, 15, 52), null);

        assertEquals(LocalDateTime.of(2026, 10, 19, 17, 7), trecho.horarioChegada());
    }

    @Test
    void ocupaOsSlotsDoIntervaloDeVoo() {
        Trecho trecho = trecho(250, 200, LocalDateTime.of(2026, 10, 19, 15, 52), null);

        assertEquals(List.of(
                new Slot(LocalDateTime.of(2026, 10, 19, 15, 0)),
                new Slot(LocalDateTime.of(2026, 10, 19, 16, 0)),
                new Slot(LocalDateTime.of(2026, 10, 19, 17, 0))
        ), trecho.slotsOcupados());
    }

    @Test
    void ocupaUmSlotQuandoVooFechaNaHoraCheia() {
        Trecho trecho = trecho(200, 200, LocalDateTime.of(2026, 10, 19, 15, 0), null);

        assertEquals(List.of(new Slot(LocalDateTime.of(2026, 10, 19, 15, 0))),
                trecho.slotsOcupados());
    }

    @Test
    void guardaPousoQuandoInformado() {
        Pouso pouso = new Pouso(2, 30);
        Trecho trecho = trecho(200, 200, LocalDateTime.of(2026, 10, 19, 15, 0), pouso);

        assertEquals(pouso, trecho.getPouso());
        assertEquals(LocalDateTime.of(2026, 10, 19, 16, 30),
                trecho.getPouso().saida(trecho.horarioChegada()));
    }

    @Test
    void sobrevooNaoTemPouso() {
        Trecho trecho = trecho(200, 200, LocalDateTime.of(2026, 10, 19, 15, 0), null);

        assertNull(trecho.getPouso());
    }

    @Test
    void recusaOrdemOuAltitudeInvalidas() {
        Aerovia aerovia = aerovia(200);
        LocalDateTime entrada = LocalDateTime.of(2026, 10, 19, 15, 0);

        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(0, aerovia, 25_000, entrada, 200, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(1, aerovia, 24_000, entrada, 200, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(1, aerovia, 25_500, entrada, 200, null));
    }

    private static Trecho trecho(double distanciaKm, double velocidadeKmh,
                                 LocalDateTime entrada, Pouso pouso) {
        return new Trecho(1, aerovia(distanciaKm), 28_000, entrada, velocidadeKmh, pouso);
    }

    private static Aerovia aerovia(double distanciaKm) {
        Aeroporto origem = new Aeroporto("POA", "Porto Alegre", 10);
        Aeroporto destino = new Aeroporto("FLN", "Florianopolis", 8);
        return new Aerovia("R1", origem, destino, distanciaKm);
    }
}
