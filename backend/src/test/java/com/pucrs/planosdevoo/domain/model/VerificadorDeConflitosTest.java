package com.pucrs.planosdevoo.domain.model;

import com.pucrs.planosdevoo.domain.fakes.ReservasEmMemoria;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Conflito = pelo menos um slot em comum, na mesma altitude da mesma aerovia ou
// na mesma vaga do mesmo aeroporto. Sem Spring e sem banco.
class VerificadorDeConflitosTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);

    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);

    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);

    private ReservasEmMemoria reservas;
    private VerificadorDeConflitos verificador;

    @BeforeEach
    void preparar() {
        reservas = new ReservasEmMemoria();
        verificador = new VerificadorDeConflitos(reservas);
    }

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    // POA -> FLN (sobrevoo) -> CWB (pouso na vaga 1, 30 min), partindo às 10:00, a 30.000 pés.
    private static PlanoDeVoo planoValido(Long id) {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(id, PILOTO, PASSAGEIROS, POA, CWB, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);
        p.adicionarTrecho(R4, 30_000, em(10, 26), new Pouso(1, 30));
        return p;
    }

    private void reservarComoOutroPlano(PlanoDeVoo outro) {
        reservas.reservar(outro.reservasDeAerovia(), outro.reservasDeVaga());
    }

    @Test
    void semReservasDeOutrosPlanosNaoHaConflito() {
        assertTrue(verificador.verificar(planoValido(1L)).isEmpty());
    }

    @Test
    void mesmaAltitudeNaMesmaAeroviaEMesmoSlotEConflito() {
        reservarComoOutroPlano(planoValido(2L));

        List<String> problemas = verificador.verificar(planoValido(1L));

        assertTrue(problemas.stream().anyMatch(p -> p.contains("Trecho 1") && p.contains("30000")
                && p.contains("R1")), problemas.toString());
    }

    @Test
    void outraAltitudeNaMesmaAeroviaNaoEConflito() {
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R1, 31_000, em(10, 0), new Pouso(9, 10));
        reservarComoOutroPlano(outro);

        List<String> problemas = verificador.verificar(planoValido(1L));

        assertTrue(problemas.stream().noneMatch(p -> p.contains("R1")), problemas.toString());
    }

    @Test
    void mesmaAltitudeEmOutroSlotNaoEConflito() {
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, FLN, em(14, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R1, 30_000, em(14, 0), new Pouso(9, 10));
        reservarComoOutroPlano(outro);

        assertTrue(verificador.verificar(planoValido(1L)).isEmpty());
    }

    @Test
    void usoParcialOcupaOSlotInteiro() {
        // O outro plano usa a R1 a 30.000 pés das 10:50 às 11:16: pega o slot das 10:00 inteiro,
        // mesmo sem nenhum minuto em comum com o uso das 10:00 às 10:26.
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 50),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R1, 30_000, em(10, 50), new Pouso(9, 10));
        reservarComoOutroPlano(outro);

        List<String> problemas = verificador.verificar(planoValido(1L));

        assertTrue(problemas.stream().anyMatch(p -> p.contains("Trecho 1") && p.contains("R1")),
                problemas.toString());
    }

    @Test
    void mesmaVagaNoMesmoAeroportoEMesmoSlotEConflito() {
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, FLN, CWB, em(10, 30),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R4, 33_000, em(10, 30), new Pouso(1, 20));
        reservarComoOutroPlano(outro);

        List<String> problemas = verificador.verificar(planoValido(1L));

        assertEquals(1, problemas.size(), problemas.toString());
        assertTrue(problemas.get(0).contains("vaga 1") && problemas.get(0).contains("CWB"), problemas.toString());
    }

    @Test
    void outraVagaNoMesmoAeroportoNaoEConflito() {
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, FLN, CWB, em(10, 30),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R4, 33_000, em(10, 30), new Pouso(2, 20));
        reservarComoOutroPlano(outro);

        assertTrue(verificador.verificar(planoValido(1L)).isEmpty());
    }

    @Test
    void doisTrechosDoMesmoPlanoNaMesmaAltitudeESlotSaoConflito() {
        // R1 às 10:00 e depois R1 de novo (voltando via R8 seria o normal; aqui basta repetir a aerovia)
        // às 10:40, a 30.000 pés: os dois caem no slot das 10:00.
        PlanoDeVoo p = PlanoDeVoo.reconstituir(1L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(1, 14));
        p.adicionarTrecho(R1, 30_000, em(10, 40), new Pouso(2, 10));

        List<String> problemas = verificador.verificar(p);

        assertTrue(problemas.stream().anyMatch(s -> s.contains("Trecho 2") && s.contains("deste plano")),
                problemas.toString());
    }

    @Test
    void verificarNaoReservaNada() {
        reservarComoOutroPlano(planoValido(2L));
        int aerovias = reservas.aerovias.size();
        int vagas = reservas.vagas.size();

        verificador.verificar(planoValido(1L));

        assertEquals(aerovias, reservas.aerovias.size());
        assertEquals(vagas, reservas.vagas.size());
    }
}
