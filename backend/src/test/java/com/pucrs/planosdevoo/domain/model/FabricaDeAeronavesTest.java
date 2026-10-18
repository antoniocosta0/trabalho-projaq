package com.pucrs.planosdevoo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricaDeAeronavesTest {

    private static DadosAeronave dados(String categoria, Integer passageiros, Double toneladas) {
        return new DadosAeronave("PR-TST", categoria, 800, 4000, "Oficina", "Companhia", passageiros, toneladas);
    }

    @Test
    void criaASubclasseDaCategoria() {
        assertInstanceOf(AeronaveParticular.class, FabricaDeAeronaves.criar(dados("PARTICULAR", null, null)));
        assertInstanceOf(AeronavePassageiros.class, FabricaDeAeronaves.criar(dados("PASSAGEIROS", 150, null)));
        assertInstanceOf(AeronaveCarga.class, FabricaDeAeronaves.criar(dados("CARGA", null, 20.0)));
    }

    @Test
    void aeronaveCriadaSegueARegraDaSuaCategoria() {
        Aeronave particular = FabricaDeAeronaves.criar(dados("PARTICULAR", null, null));

        assertEquals("PARTICULAR", particular.getCategoria());
        assertEquals(800, particular.getVelocidadeCruzeiroKmh());
        assertEquals(4000 * 0.85, particular.distanciaMaxSemAbastecer(), 1e-9);
    }

    @Test
    void categoriaDesconhecidaERecusada() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> FabricaDeAeronaves.criar(dados("HELICOPTERO", null, null)));
        assertTrue(e.getMessage().contains("HELICOPTERO"), e.getMessage());
    }

    @Test
    void campoProprioDaCategoriaEObrigatorio() {
        IllegalArgumentException semPassageiros = assertThrows(IllegalArgumentException.class,
                () -> FabricaDeAeronaves.criar(dados("PASSAGEIROS", null, null)));
        assertTrue(semPassageiros.getMessage().contains("capacidadePassageiros"), semPassageiros.getMessage());

        IllegalArgumentException semToneladas = assertThrows(IllegalArgumentException.class,
                () -> FabricaDeAeronaves.criar(dados("CARGA", null, null)));
        assertTrue(semToneladas.getMessage().contains("capacidadeToneladas"), semToneladas.getMessage());
    }
}
