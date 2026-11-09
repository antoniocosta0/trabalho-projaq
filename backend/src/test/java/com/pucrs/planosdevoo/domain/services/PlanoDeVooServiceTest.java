package com.pucrs.planosdevoo.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.planosdevoo.domain.exceptions.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.exceptions.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.fakes.PlanoDeVooRepositoryEmMemoria;
import com.pucrs.planosdevoo.domain.fakes.ReservaRepositoryEmMemoria;
import com.pucrs.planosdevoo.domain.fakes.TransacaoEmMemoria;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanoDeVooServiceTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);
    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);
    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);
    private static final LocalDateTime VESPERA = LocalDateTime.of(2026, 10, 18, 12, 0);

    private PlanoDeVooRepositoryEmMemoria planos;
    private ReservaRepositoryEmMemoria reservas;
    private DisponibilidadeService disponibilidade;

    @BeforeEach
    void preparar() {
        planos = new PlanoDeVooRepositoryEmMemoria();
        reservas = new ReservaRepositoryEmMemoria();
        disponibilidade = new DisponibilidadeService(aeroviaRepository(), null, reservas);
    }

    @Test
    void validarNaoReservaNemMudaASituacao() {
        planoValido(1L, em(10, 0));

        assertTrue(servicoEm(VESPERA).validar(1L).isEmpty());

        assertTrue(reservas.aerovias.isEmpty());
        assertTrue(reservas.vagas.isEmpty());
        assertEquals(SituacaoPlano.EM_EDICAO, situacaoDo(1L));
    }

    @Test
    void aprovarReservaTodosOsRecursos() {
        planoValido(1L, em(10, 0));

        servicoEm(VESPERA).aprovar(1L);

        assertEquals(SituacaoPlano.APROVADO, situacaoDo(1L));
        assertEquals(2, reservas.aerovias.size());
        assertEquals(2, reservas.vagas.size());
        assertFalse(altitudesLivresEmR1().contains(30_000));
    }

    @Test
    void aprovaPlanoComPartidaJaOcorrida() {
        planoValido(1L, em(10, 0));

        servicoEm(em(12, 0)).aprovar(1L);

        assertEquals(SituacaoPlano.APROVADO, situacaoDo(1L));
    }

    @Test
    void aprovacaoRecusadaNaoDeixaReservaParcial() {
        planoValido(1L, em(10, 0));
        servicoEm(VESPERA).aprovar(1L);
        PlanoDeVoo outro = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, CWB, em(10, 5),
                SituacaoPlano.EM_EDICAO, List.of());
        outro.adicionarTrecho(R1, 31_000, em(10, 5), null);
        outro.adicionarTrecho(R4, 31_000, em(10, 31), new Pouso(1, 30));
        planos.adicionar(outro);

        PlanoInvalidoException erro = assertThrows(PlanoInvalidoException.class,
                () -> servicoEm(VESPERA).aprovar(2L));

        assertTrue(erro.getProblemas().stream().anyMatch(p -> p.contains("vaga 1")), erro.getProblemas().toString());
        assertEquals(SituacaoPlano.EM_EDICAO, situacaoDo(2L));
        assertTrue(reservas.aerovias.stream().noneMatch(r -> r.getPlanoId().equals(2L)));
        assertTrue(reservas.vagas.stream().noneMatch(r -> r.getPlanoId().equals(2L)));
    }

    @Test
    void validarConsideraAsReservasAtuais() {
        planoValido(1L, em(10, 0));
        planoValido(2L, em(10, 0));
        servicoEm(VESPERA).aprovar(1L);

        assertFalse(servicoEm(VESPERA).validar(2L).isEmpty());
    }

    @Test
    void cancelarPlanoFuturoLiberaAsReservas() {
        planoValido(1L, em(10, 0));
        servicoEm(VESPERA).aprovar(1L);

        servicoEm(VESPERA).cancelar(1L);

        assertEquals(SituacaoPlano.CANCELADO, situacaoDo(1L));
        assertTrue(reservas.aerovias.isEmpty());
        assertTrue(reservas.vagas.isEmpty());
        assertTrue(altitudesLivresEmR1().contains(30_000));
    }

    @Test
    void naoCancelaPlanoQueJaPartiu() {
        planoValido(1L, em(10, 0));
        servicoEm(VESPERA).aprovar(1L);

        assertThrows(OperacaoNaoPermitidaException.class, () -> servicoEm(em(11, 0)).cancelar(1L));

        assertEquals(SituacaoPlano.APROVADO, situacaoDo(1L));
        assertEquals(2, reservas.aerovias.size());
    }

    private PlanoDeVooService servicoEm(LocalDateTime agora) {
        ZoneId zona = ZoneId.of("America/Sao_Paulo");
        Clock relogio = Clock.fixed(agora.atZone(zona).toInstant(), zona);
        return new PlanoDeVooService(planos, null, null, null, null, reservas, disponibilidade,
                new TransacaoEmMemoria(), relogio);
    }

    private void planoValido(Long id, LocalDateTime partida) {
        PlanoDeVoo plano = PlanoDeVoo.reconstituir(id, PILOTO, PASSAGEIROS, POA, CWB, partida,
                SituacaoPlano.EM_EDICAO, List.of());
        plano.adicionarTrecho(R1, 30_000, partida, null);
        plano.adicionarTrecho(R4, 30_000, partida.plusMinutes(26), new Pouso(1, 30));
        planos.adicionar(plano);
    }

    private List<Integer> altitudesLivresEmR1() {
        return disponibilidade.altitudesDisponiveis("R1", em(10, 0), em(11, 0));
    }

    private SituacaoPlano situacaoDo(Long id) {
        return planos.buscarPorId(id).orElseThrow().getSituacao();
    }

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    private static AeroviaRepository aeroviaRepository() {
        return new AeroviaRepository() {
            public List<Aerovia> listarTodas() { return List.of(R1, R4); }
            public Optional<Aerovia> buscarPorId(String id) {
                return listarTodas().stream().filter(a -> a.getId().equals(id)).findFirst();
            }
            public Aerovia salvar(Aerovia aerovia) { return aerovia; }
        };
    }
}
