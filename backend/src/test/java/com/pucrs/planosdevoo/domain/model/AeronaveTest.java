package com.pucrs.planosdevoo.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AeronaveTest {

    private static final Aeronave PARTICULAR = new AeronaveParticular("PP-PAR", 500, 2000, "Manutec");
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);
    private static final Aeronave CARGA = new AeronaveCarga("PR-CRG", 750, 4000, "Sul Cargo", 20);

    @Test
    void particularSoVoaEntre25000E27000() {
        assertTrue(PARTICULAR.podeOperar(27_000, em(10, 0), em(11, 0)));
        assertFalse(PARTICULAR.podeOperar(28_000, em(10, 0), em(11, 0)));
    }

    @Test
    void passageirosSoVoaAPartirDe28000() {
        assertTrue(PASSAGEIROS.podeOperar(28_000, em(10, 0), em(11, 0)));
        assertFalse(PASSAGEIROS.podeOperar(27_000, em(10, 0), em(11, 0)));
    }

    @Test
    void cargaSoVoaEntreMeiaNoiteESeisHoras() {
        assertTrue(CARGA.podeOperar(25_000, em(0, 0), em(5, 59)));
        assertFalse(CARGA.podeOperar(25_000, em(5, 0), em(6, 0)));
        assertFalse(CARGA.podeOperar(25_000, em(10, 0), em(11, 0)));
    }

    @Test
    void autonomiaUtilEh85PorCentoDaNominal() {
        assertEquals(4250, PASSAGEIROS.distanciaMaxSemAbastecer(), 0.001);
    }

    @Test
    void fabricaCriaASubclasseDaCategoria() {
        Aeronave aeronave = AeronaveFactory.criar("CARGA", "PR-NEW", 700, 3000, null, "Sul Cargo", null, 15.0);

        assertTrue(aeronave instanceof AeronaveCarga);
    }

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }
}
