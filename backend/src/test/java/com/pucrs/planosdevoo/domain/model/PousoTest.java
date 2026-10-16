package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PousoTest {

    @Test
    void saidaIncluiPermanenciaAposChegada() {
        Pouso pouso = new Pouso(1, 20);
        LocalDateTime chegada = LocalDateTime.of(2026, 10, 19, 15, 50);

        assertEquals(LocalDateTime.of(2026, 10, 19, 16, 10), pouso.saida(chegada));
    }

    @Test
    void recusaVagaOuPermanenciaNaoPositivas() {
        assertThrows(IllegalArgumentException.class, () -> new Pouso(0, 20));
        assertThrows(IllegalArgumentException.class, () -> new Pouso(1, 0));
    }

    @Test
    void comparaPorValor() {
        Pouso um = new Pouso(3, 45);
        Pouso outro = new Pouso(3, 45);

        assertEquals(um, outro);
        assertEquals(um.hashCode(), outro.hashCode());
    }
}
