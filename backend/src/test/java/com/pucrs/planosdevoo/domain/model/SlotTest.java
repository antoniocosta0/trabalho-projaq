package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlotTest {

    @Test
    void ocupaTresSlotsNoExemploDoEnunciado() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 19, 15, 52);
        LocalDateTime fim = LocalDateTime.of(2026, 10, 19, 17, 7);

        assertEquals(List.of(
                new Slot(LocalDateTime.of(2026, 10, 19, 15, 0)),
                new Slot(LocalDateTime.of(2026, 10, 19, 16, 0)),
                new Slot(LocalDateTime.of(2026, 10, 19, 17, 0))
        ), Slot.entre(inicio, fim));
    }

    @Test
    void ocupaUmSlotQuandoIntervaloFechaNaHoraCheia() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 19, 15, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 10, 19, 16, 0);

        assertEquals(List.of(new Slot(LocalDateTime.of(2026, 10, 19, 15, 0))),
                Slot.entre(inicio, fim));
    }

    @Test
    void ocupaDoisSlotsNoExemploDeVagaDoEnunciado() {
        LocalDateTime chegada = LocalDateTime.of(2026, 10, 19, 15, 50);
        LocalDateTime saida = chegada.plusMinutes(20);

        assertEquals(List.of(
                new Slot(LocalDateTime.of(2026, 10, 19, 15, 0)),
                new Slot(LocalDateTime.of(2026, 10, 19, 16, 0))
        ), Slot.entre(chegada, saida));
    }

    @Test
    void alinhaInicioParaHoraCheia() {
        Slot slot = new Slot(LocalDateTime.of(2026, 10, 19, 15, 52, 30));

        assertEquals(LocalDateTime.of(2026, 10, 19, 15, 0), slot.getInicio());
    }

    @Test
    void comparaPorValor() {
        Slot um = new Slot(LocalDateTime.of(2026, 10, 19, 15, 52));
        Slot outro = new Slot(LocalDateTime.of(2026, 10, 19, 15, 10));

        assertEquals(um, outro);
        assertEquals(um.hashCode(), outro.hashCode());
    }

    @Test
    void recusaFimQueNaoEPosteriorAoInicio() {
        LocalDateTime instante = LocalDateTime.of(2026, 10, 19, 15, 0);

        assertThrows(IllegalArgumentException.class, () -> Slot.entre(instante, instante));
        assertThrows(IllegalArgumentException.class,
                () -> Slot.entre(instante, instante.minusMinutes(1)));
    }
}
