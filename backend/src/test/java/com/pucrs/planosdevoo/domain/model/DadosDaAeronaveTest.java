package com.pucrs.planosdevoo.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// Cada categoria responde pelos próprios dados de cadastro, sem instanceof do
// lado de fora, e recusa no construtor o que não faz sentido para ela.
class DadosDaAeronaveTest {

    @Test
    void particularTrazEmpresaDeManutencaoENadaDeComercial() {
        DadosAeronave d = new AeronaveParticular("PP-XXX", 650, 2800, "Hangar POA").dados();

        assertEquals("PARTICULAR", d.categoria());
        assertEquals("Hangar POA", d.empresaManutencao());
        assertNull(d.companhiaAerea());
        assertNull(d.capacidadePassageiros());
        assertNull(d.capacidadeToneladas());
    }

    @Test
    void passageirosTrazCompanhiaECapacidadeDePassageiros() {
        DadosAeronave d = new AeronavePassageiros("PR-XXX", 830, 5000, "Linhas do Sul", 180).dados();

        assertEquals(180, d.capacidadePassageiros());
        assertEquals("Linhas do Sul", d.companhiaAerea());
        assertNull(d.capacidadeToneladas());
        assertNull(d.empresaManutencao());
    }

    @Test
    void cargaTrazCompanhiaECapacidadeEmToneladas() {
        DadosAeronave d = new AeronaveCarga("PR-CCC", 780, 4500, "Sul Cargas", 20).dados();

        assertEquals(20, d.capacidadeToneladas());
        assertNull(d.capacidadePassageiros());
    }

    @Test
    void capacidadeDePassageirosPrecisaSerPositiva() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new AeronavePassageiros("PR-XXX", 830, 5000, "Linhas do Sul", 0));

        assertTrue(e.getMessage().contains("capacidadePassageiros"));
    }

    @Test
    void capacidadeEmToneladasPrecisaSerPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new AeronaveCarga("PR-CCC", 780, 4500, "Sul Cargas", -1));
    }

    @Test
    void companhiaAereaEObrigatoriaNasComerciais() {
        assertThrows(IllegalArgumentException.class,
                () -> new AeronavePassageiros("PR-XXX", 830, 5000, " ", 180));
        assertThrows(IllegalArgumentException.class,
                () -> new AeronaveCarga("PR-CCC", 780, 4500, null, 20));
    }

    @Test
    void empresaDeManutencaoEObrigatoriaNaParticular() {
        assertThrows(IllegalArgumentException.class,
                () -> new AeronaveParticular("PP-XXX", 650, 2800, null));
    }
}
