package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.fakes.PlanosEmMemoria;
import com.pucrs.planosdevoo.domain.fakes.ReservasEmMemoria;
import com.pucrs.planosdevoo.domain.fakes.TransacaoDireta;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.VerificadorDeConflitos;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Casos de uso de aprovação, validação e cancelamento, com repositórios em
// memória: sem Spring e sem banco. A atomicidade com banco de verdade está em
// AprovacaoAtomicaTest.
class AprovarECancelarPlanoUCTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);

    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);

    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private PlanosEmMemoria planos;
    private ReservasEmMemoria reservas;
    private AprovarPlanoUC aprovar;
    private CancelarPlanoUC cancelar;
    private ValidarPlanoUC validar;

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    private static Clock relogioEm(LocalDateTime agora) {
        return Clock.fixed(agora.atZone(ZONA).toInstant(), ZONA);
    }

    // Véspera da data de referência: todos os planos destes testes partem em
    // 19/10, então na hora de aprovar eles ainda estão no futuro.
    private static final LocalDateTime VESPERA = LocalDateTime.of(2026, 10, 18, 12, 0);

    @BeforeEach
    void preparar() {
        planos = new PlanosEmMemoria();
        reservas = new ReservasEmMemoria();
        VerificadorDeConflitos verificador = new VerificadorDeConflitos(reservas);
        aprovar = new AprovarPlanoUC(planos, reservas, verificador, new TransacaoDireta(), relogioEm(VESPERA));
        validar = new ValidarPlanoUC(planos, verificador);
        cancelar = cancelarCom(relogioEm(em(8, 0)));
    }

    private CancelarPlanoUC cancelarCom(Clock relogio) {
        return new CancelarPlanoUC(planos, reservas, new TransacaoDireta(), relogio);
    }

    // POA -> FLN (sobrevoo) -> CWB (pouso na vaga 1, 30 min), a 30.000 pés.
    private PlanoDeVoo planoValido(Long id, LocalDateTime partida) {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(id, PILOTO, PASSAGEIROS, POA, CWB, partida,
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, partida, null);
        p.adicionarTrecho(R4, 30_000, partida.plusMinutes(26), new Pouso(1, 30));
        planos.adicionar(p);
        return p;
    }

    // ---------- aprovar (UC10) ----------

    @Test
    void aprovarMudaParaAprovadoEGravaTodasAsReservas() {
        planoValido(1L, em(10, 0));

        PlanoDeVoo aprovado = aprovar.executar(1L);

        assertEquals(SituacaoPlano.APROVADO, aprovado.getSituacao());
        assertEquals(SituacaoPlano.APROVADO, planos.buscarPorId(1L).orElseThrow().getSituacao());
        assertEquals(2, reservas.aerovias.size());
        assertEquals(2, reservas.vagas.size());
    }

    @Test
    void aprovacaoRecusadaPorConflitoNaoDeixaNenhumaReservaNemMudaASituacao() {
        planoValido(1L, em(10, 0));
        planoValido(2L, em(10, 0));
        aprovar.executar(1L);
        int aerovias = reservas.aerovias.size();
        int vagas = reservas.vagas.size();

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class, () -> aprovar.executar(2L));

        assertFalse(e.getProblemas().isEmpty());
        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(2L).orElseThrow().getSituacao());
        assertEquals(aerovias, reservas.aerovias.size());
        assertEquals(vagas, reservas.vagas.size());
        assertTrue(reservas.aerovias.stream().allMatch(r -> r.getPlanoId().equals(1L)));
    }

    @Test
    void conflitoSoNoSegundoTrechoTambemNaoReservaOPrimeiro() {
        planoValido(1L, em(10, 0));
        aprovar.executar(1L);
        // O plano 2 parte mais tarde (R1 livre), mas pousa na mesma vaga 1 de CWB no mesmo slot.
        PlanoDeVoo p2 = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, CWB, em(10, 5),
                SituacaoPlano.EM_EDICAO, List.of());
        p2.adicionarTrecho(R1, 31_000, em(10, 5), null);
        p2.adicionarTrecho(R4, 31_000, em(10, 31), new Pouso(1, 30));
        planos.adicionar(p2);

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class, () -> aprovar.executar(2L));

        assertTrue(e.getProblemas().stream().anyMatch(s -> s.contains("vaga 1")), e.getProblemas().toString());
        assertTrue(reservas.aerovias.stream().noneMatch(r -> r.getPlanoId().equals(2L)));
        assertTrue(reservas.vagas.stream().noneMatch(r -> r.getPlanoId().equals(2L)));
    }

    @Test
    void aprovarPlanoInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> aprovar.executar(99L));
    }

    @Test
    void naoAprovaDuasVezes() {
        planoValido(1L, em(10, 0));
        aprovar.executar(1L);

        assertThrows(OperacaoNaoPermitidaException.class, () -> aprovar.executar(1L));
        assertEquals(2, reservas.aerovias.size());
    }

    // ---------- validar (UC09) ----------

    @Test
    void validarMostraOConflitoMasNaoReservaNemMudaASituacao() {
        planoValido(1L, em(10, 0));
        planoValido(2L, em(10, 0));
        aprovar.executar(1L);
        int aerovias = reservas.aerovias.size();
        int vagas = reservas.vagas.size();

        List<String> problemas = validar.executar(2L);

        assertFalse(problemas.isEmpty());
        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(2L).orElseThrow().getSituacao());
        assertEquals(aerovias, reservas.aerovias.size());
        assertEquals(vagas, reservas.vagas.size());
    }

    @Test
    void validarPlanoLivreNaoTemProblemaENaoReservaNada() {
        planoValido(1L, em(10, 0));

        assertTrue(validar.executar(1L).isEmpty());
        assertTrue(reservas.aerovias.isEmpty());
        assertTrue(reservas.vagas.isEmpty());
        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(1L).orElseThrow().getSituacao());
    }

    // ---------- cancelar (UC12) ----------

    @Test
    void cancelarPlanoFuturoLiberaTodasAsReservas() {
        planoValido(1L, em(10, 0));
        planoValido(2L, em(14, 0));
        aprovar.executar(1L);
        aprovar.executar(2L);

        PlanoDeVoo cancelado = cancelar.executar(1L);

        assertEquals(SituacaoPlano.CANCELADO, cancelado.getSituacao());
        assertTrue(reservas.aerovias.stream().noneMatch(r -> r.getPlanoId().equals(1L)));
        assertTrue(reservas.vagas.stream().noneMatch(r -> r.getPlanoId().equals(1L)));
        // O outro plano continua com as reservas dele.
        assertEquals(2, reservas.aerovias.size());
        assertEquals(2, reservas.vagas.size());
    }

    @Test
    void depoisDeCancelarOsRecursosVoltamAFicarLivres() {
        planoValido(1L, em(10, 0));
        planoValido(2L, em(10, 0));
        aprovar.executar(1L);
        cancelar.executar(1L);

        PlanoDeVoo aprovado = aprovar.executar(2L);

        assertEquals(SituacaoPlano.APROVADO, aprovado.getSituacao());
    }

    @Test
    void naoCancelaPlanoComPartidaJaOcorridaEMantemAsReservas() {
        planoValido(1L, em(10, 0));
        aprovar.executar(1L);
        CancelarPlanoUC depoisDaPartida = cancelarCom(relogioEm(em(11, 0)));

        assertThrows(OperacaoNaoPermitidaException.class, () -> depoisDaPartida.executar(1L));

        assertEquals(SituacaoPlano.APROVADO, planos.buscarPorId(1L).orElseThrow().getSituacao());
        assertEquals(2, reservas.aerovias.size());
        assertEquals(2, reservas.vagas.size());
    }

    @Test
    void naoCancelaPlanoQueNaoEstaAprovado() {
        planoValido(1L, em(10, 0));

        assertThrows(OperacaoNaoPermitidaException.class, () -> cancelar.executar(1L));
    }

    @Test
    void cancelarPlanoInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> cancelar.executar(99L));
    }
}
