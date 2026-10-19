package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Aprovação, cancelamento e as reservas que o plano pede. Regra pura: sem Spring e sem banco.
class ReservasDoPlanoTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);

    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);

    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");
    // 830 km/h: R1 leva 26 min e R4 leva 18 min.
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    // "Agora" na véspera: todo plano destes testes ainda está no futuro.
    private static final LocalDateTime ANTES_DA_PARTIDA = LocalDateTime.of(2026, 10, 18, 12, 0);

    // POA -> FLN (sobrevoo) -> CWB (pouso na vaga 1, 30 min), partindo às 10:00.
    private static PlanoDeVoo planoValido(SituacaoPlano situacao, LocalDateTime partida) {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(7L, PILOTO, PASSAGEIROS, POA, CWB, partida,
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, partida, null);
        p.adicionarTrecho(R4, 30_000, partida.plusMinutes(26), new Pouso(1, 30));
        // O percurso só se monta em edição; depois o plano é reconstituído na situação pedida.
        return PlanoDeVoo.reconstituir(7L, PILOTO, PASSAGEIROS, POA, CWB, partida, situacao, p.getTrechos());
    }

    // ---------- reservas que o plano pede ----------

    @Test
    void umaReservaDeAeroviaPorTrechoEPorSlot() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        List<ReservaAerovia> reservas = p.reservasDeAerovia();

        // R1 10:00-10:26 e R4 10:26-10:44 cabem, cada um, no slot das 10:00.
        assertEquals(2, reservas.size());
        assertEquals("R1", reservas.get(0).getAerovia().getId());
        assertEquals("R4", reservas.get(1).getAerovia().getId());
        assertEquals(30_000, reservas.get(0).getAltitude());
        assertEquals(new Slot(em(10, 0)), reservas.get(0).getSlot());
        assertEquals(7L, reservas.get(0).getPlanoId());
    }

    @Test
    void trechoQueCruzaAHoraCheiaReservaOsDoisSlots() {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(7L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 50),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(10, 50), new Pouso(2, 10));

        // 10:50 -> 11:16: ocupa 10:00 e 11:00.
        assertEquals(List.of(new Slot(em(10, 0)), new Slot(em(11, 0))),
                p.reservasDeAerovia().stream().map(ReservaAerovia::getSlot).toList());
    }

    @Test
    void reservaDeVagaCobreDaChegadaAoFimDaPermanenciaNoAeroportoDeDestino() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        List<ReservaVaga> reservas = p.reservasDeVaga();

        // Chegada 10:44 + 30 min = 11:14: slots das 10:00 e das 11:00, só no pouso (CWB, vaga 1).
        assertEquals(2, reservas.size());
        assertTrue(reservas.stream().allMatch(r -> r.getAeroporto().getCodigo().equals("CWB")));
        assertTrue(reservas.stream().allMatch(r -> r.getNumeroVaga() == 1));
        assertEquals(List.of(new Slot(em(10, 0)), new Slot(em(11, 0))),
                reservas.stream().map(ReservaVaga::getSlot).toList());
    }

    @Test
    void sobrevooNaoReservaVaga() {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(7L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);

        assertTrue(p.reservasDeVaga().isEmpty());
    }

    @Test
    void altitudesPermitidasSaoAsOnzeDe25000A35000() {
        List<Integer> altitudes = Trecho.altitudesPermitidas();

        assertEquals(11, altitudes.size());
        assertEquals(25_000, altitudes.get(0));
        assertEquals(26_000, altitudes.get(1));
        assertEquals(35_000, altitudes.get(10));
    }

    // ---------- aprovar (UC10) ----------

    @Test
    void aprovarPlanoValidoSemConflitoMudaParaAprovado() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        p.aprovar(List.of(), ANTES_DA_PARTIDA);

        assertEquals(SituacaoPlano.APROVADO, p.getSituacao());
    }

    @Test
    void aprovarComConflitoDeReservaRecusaECarregaOsProblemas() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class,
                () -> p.aprovar(List.of("Trecho 1: altitude ocupada"), ANTES_DA_PARTIDA));

        assertEquals(List.of("Trecho 1: altitude ocupada"), e.getProblemas());
        assertEquals(SituacaoPlano.EM_EDICAO, p.getSituacao());
    }

    @Test
    void aprovarPlanoComProblemaProprioRecusaMesmoSemConflito() {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(7L, PILOTO, PASSAGEIROS, POA, CWB, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class, () -> p.aprovar(List.of(), ANTES_DA_PARTIDA));

        assertTrue(e.getProblemas().get(0).contains("nenhum trecho"));
        assertEquals(SituacaoPlano.EM_EDICAO, p.getSituacao());
    }

    @Test
    void naoAprovaPlanoCujaPartidaJaOcorreu() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));
        LocalDateTime depoisDaPartida = em(10, 1);

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class,
                () -> p.aprovar(List.of(), depoisDaPartida));

        assertTrue(e.getProblemas().stream().anyMatch(m -> m.contains("já ocorreu")));
        assertEquals(SituacaoPlano.EM_EDICAO, p.getSituacao());
    }

    @Test
    void aprovaPlanoQueParteNoInstanteSeguinteAoAgora() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        p.aprovar(List.of(), em(9, 59));

        assertEquals(SituacaoPlano.APROVADO, p.getSituacao());
    }

    @Test
    void naoAprovaPlanoQueParteExatamenteAgora() {
        PlanoDeVoo p = planoValido(SituacaoPlano.EM_EDICAO, em(10, 0));

        assertThrows(PlanoInvalidoException.class, () -> p.aprovar(List.of(), em(10, 0)));
    }

    @Test
    void soPlanoEmEdicaoPodeSerAprovado() {
        PlanoDeVoo p = planoValido(SituacaoPlano.APROVADO, em(10, 0));

        assertThrows(OperacaoNaoPermitidaException.class, () -> p.aprovar(List.of(), ANTES_DA_PARTIDA));
    }

    // ---------- cancelar (UC12) ----------

    @Test
    void cancelarPlanoAprovadoComPartidaFuturaMudaParaCancelado() {
        PlanoDeVoo p = planoValido(SituacaoPlano.APROVADO, em(10, 0));

        p.cancelar(em(9, 59));

        assertEquals(SituacaoPlano.CANCELADO, p.getSituacao());
    }

    @Test
    void naoCancelaPlanoComPartidaJaOcorrida() {
        PlanoDeVoo p = planoValido(SituacaoPlano.APROVADO, em(10, 0));

        OperacaoNaoPermitidaException e = assertThrows(OperacaoNaoPermitidaException.class,
                () -> p.cancelar(em(10, 1)));

        assertTrue(e.getMessage().contains("já ocorreu"));
        assertEquals(SituacaoPlano.APROVADO, p.getSituacao());
    }

    @Test
    void naoCancelaNoInstanteExatoDaPartida() {
        PlanoDeVoo p = planoValido(SituacaoPlano.APROVADO, em(10, 0));

        assertThrows(OperacaoNaoPermitidaException.class, () -> p.cancelar(em(10, 0)));
    }

    @Test
    void soPlanoAprovadoPodeSerCancelado() {
        for (SituacaoPlano situacao : List.of(SituacaoPlano.EM_EDICAO, SituacaoPlano.CANCELADO,
                SituacaoPlano.ABANDONADO)) {
            PlanoDeVoo p = planoValido(situacao, em(10, 0));

            assertThrows(OperacaoNaoPermitidaException.class, () -> p.cancelar(em(8, 0)),
                    "plano " + situacao + " não deveria poder ser cancelado");
        }
    }
}
