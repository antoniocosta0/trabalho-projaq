package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.fakes.ReservasEmMemoria;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// UC07 e UC08. Sem Spring e sem banco; só consultam, nunca reservam.
class ConsultarDisponibilidadeUCTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);

    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);

    private ReservasEmMemoria reservas;
    private ConsultarAltitudesDisponiveisUC altitudes;
    private ConsultarVagasDisponiveisUC vagas;

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    @BeforeEach
    void preparar() {
        reservas = new ReservasEmMemoria();
        RepositorioAerovias aerovias = mock(RepositorioAerovias.class);
        when(aerovias.buscarPorId("R1")).thenReturn(Optional.of(R1));
        when(aerovias.buscarPorId("R99")).thenReturn(Optional.empty());
        RepositorioAeroportos aeroportos = mock(RepositorioAeroportos.class);
        when(aeroportos.buscarPorCodigo("FLN")).thenReturn(Optional.of(FLN));
        when(aeroportos.buscarPorCodigo("XXX")).thenReturn(Optional.empty());
        altitudes = new ConsultarAltitudesDisponiveisUC(aerovias, reservas);
        vagas = new ConsultarVagasDisponiveisUC(aeroportos, reservas);
    }

    // Plano que usa a R1 a 30.000 pés das 10:00 às 10:26 e pousa em FLN na vaga 5 por 20 min.
    private void ocuparComoOutroPlano() {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(2L, PILOTO, PASSAGEIROS, POA, FLN, em(10, 0),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(5, 20));
        reservas.reservar(p.reservasDeAerovia(), p.reservasDeVaga());
    }

    // ---------- UC07: altitudes ----------

    @Test
    void semReservasAsOnzeAltitudesEstaoLivres() {
        List<Integer> livres = altitudes.executar("R1", em(10, 0), em(11, 0));

        assertEquals(11, livres.size());
    }

    @Test
    void altitudeReservadaNoPeriodoSaiDaLista() {
        ocuparComoOutroPlano();

        List<Integer> livres = altitudes.executar("R1", em(10, 0), em(11, 0));

        assertEquals(10, livres.size());
        assertFalse(livres.contains(30_000));
    }

    @Test
    void altitudeReservadaForaDoPeriodoContinuaLivre() {
        ocuparComoOutroPlano();

        assertTrue(altitudes.executar("R1", em(14, 0), em(15, 0)).contains(30_000));
    }

    @Test
    void periodoQueTocaSoPartedoSlotJaConsideraOSlotInteiro() {
        ocuparComoOutroPlano();

        // 10:45 às 10:50 cai dentro do slot das 10:00, que está ocupado a 30.000 pés.
        assertFalse(altitudes.executar("R1", em(10, 45), em(10, 50)).contains(30_000));
    }

    @Test
    void periodoQueTerminaNaHoraCheiaNaoTocaOSlotSeguinte() {
        ocuparComoOutroPlano();

        // [09:00, 10:00) só ocupa o slot das 09:00: o das 10:00 não entra.
        assertTrue(altitudes.executar("R1", em(9, 0), em(10, 0)).contains(30_000));
    }

    @Test
    void aeroviaInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> altitudes.executar("R99", em(10, 0), em(11, 0)));
    }

    @Test
    void periodoInvertidoERecusado() {
        assertThrows(IllegalArgumentException.class, () -> altitudes.executar("R1", em(11, 0), em(10, 0)));
    }

    @Test
    void consultarAltitudesNaoReservaNada() {
        altitudes.executar("R1", em(10, 0), em(11, 0));

        assertTrue(reservas.aerovias.isEmpty());
    }

    // ---------- UC08: vagas ----------

    @Test
    void semReservasTodasAsVagasDoAeroportoEstaoLivres() {
        List<Integer> livres = vagas.executar("FLN", em(10, 0), em(11, 0));

        assertEquals(16, livres.size());
        assertEquals(1, livres.get(0));
        assertEquals(16, livres.get(15));
    }

    @Test
    void vagaReservadaNoPeriodoSaiDaLista() {
        ocuparComoOutroPlano();

        List<Integer> livres = vagas.executar("FLN", em(10, 0), em(11, 0));

        assertEquals(15, livres.size());
        assertFalse(livres.contains(5));
    }

    @Test
    void vagaReservadaForaDoPeriodoContinuaLivre() {
        ocuparComoOutroPlano();

        assertTrue(vagas.executar("FLN", em(14, 0), em(15, 0)).contains(5));
    }

    @Test
    void aeroportoInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> vagas.executar("XXX", em(10, 0), em(11, 0)));
    }

    @Test
    void consultarVagasNaoReservaNada() {
        vagas.executar("FLN", em(10, 0), em(11, 0));

        assertTrue(reservas.vagas.isEmpty());
    }
}
