package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AprovarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CancelarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAltitudesDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarVagasDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.DefinirPercursoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Único teste que sobe o Spring: confere com banco de verdade (H2 em memória, só
// para o teste) o que o enunciado cobra por escrito — a aprovação é tudo ou nada.
// Cada teste usa um dia diferente, em 2031, para não esbarrar nos dados da carga
// inicial nem nas reservas dos outros testes.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:atomicidade;DB_CLOSE_DELAY=-1")
class AprovacaoAtomicaTest {

    @Autowired CriarPlanoUC criarPlano;
    @Autowired DefinirPercursoUC definirPercurso;
    @Autowired AprovarPlanoUC aprovarPlano;
    @Autowired CancelarPlanoUC cancelarPlano;
    @Autowired ConsultarAltitudesDisponiveisUC consultarAltitudes;
    @Autowired ConsultarVagasDisponiveisUC consultarVagas;
    @Autowired RepositorioPlanosDeVoo planos;
    @Autowired RepositorioReservas reservas;
    @Autowired RepositorioAerovias aerovias;
    @Autowired RepositorioAeroportos aeroportos;
    @Autowired Transacao transacao;

    private static LocalDateTime em(int dia, int hora, int minuto) {
        return LocalDateTime.of(2031, 3, dia, hora, minuto);
    }

    // POA -> FLN pela R1, pouso na vaga pedida por 20 min. PR-PAX voa R1 em 26 min.
    private PlanoDeVoo planoPoaFln(LocalDateTime partida, int altitude, int vaga) {
        PlanoDeVoo plano = criarPlano.executar(new CriarPlanoCommand(1L, "PR-PAX", "POA", "FLN", partida));
        return definirPercurso.adicionarTrecho(plano.getId(), new TrechoCommand("R1", altitude, partida, vaga, 20));
    }

    private List<Integer> altitudesLivres(LocalDateTime inicio, LocalDateTime fim) {
        return consultarAltitudes.executar("R1", inicio, fim);
    }

    private List<Integer> vagasLivres(LocalDateTime inicio, LocalDateTime fim) {
        return consultarVagas.executar("FLN", inicio, fim);
    }

    @Test
    void cicloCompletoAprovaReservaECancelaLiberando() {
        PlanoDeVoo plano = planoPoaFln(em(10, 10, 0), 30_000, 3);
        assertTrue(altitudesLivres(em(10, 10, 0), em(10, 11, 0)).contains(30_000));

        PlanoDeVoo aprovado = aprovarPlano.executar(plano.getId());

        assertEquals(SituacaoPlano.APROVADO, aprovado.getSituacao());
        assertEquals(SituacaoPlano.APROVADO, planos.buscarPorId(plano.getId()).orElseThrow().getSituacao());
        assertFalse(altitudesLivres(em(10, 10, 0), em(10, 11, 0)).contains(30_000));
        assertFalse(vagasLivres(em(10, 10, 0), em(10, 11, 0)).contains(3));

        PlanoDeVoo cancelado = cancelarPlano.executar(plano.getId());

        assertEquals(SituacaoPlano.CANCELADO, cancelado.getSituacao());
        assertEquals(SituacaoPlano.CANCELADO, planos.buscarPorId(plano.getId()).orElseThrow().getSituacao());
        assertTrue(altitudesLivres(em(10, 10, 0), em(10, 11, 0)).contains(30_000));
        assertTrue(vagasLivres(em(10, 10, 0), em(10, 11, 0)).contains(3));
    }

    @Test
    void aprovacaoRecusadaPelaConferenciaNaoMudaNadaNoBanco() {
        PlanoDeVoo primeiro = planoPoaFln(em(11, 10, 0), 30_000, 3);
        aprovarPlano.executar(primeiro.getId());
        PlanoDeVoo segundo = planoPoaFln(em(11, 10, 0), 30_000, 4);

        PlanoInvalidoException e = assertThrows(PlanoInvalidoException.class,
                () -> aprovarPlano.executar(segundo.getId()));

        assertTrue(e.getProblemas().stream().anyMatch(p -> p.contains("30000")), e.getProblemas().toString());
        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(segundo.getId()).orElseThrow().getSituacao());
        // A vaga 4 do segundo plano não ficou reservada: só a do primeiro (3) está ocupada.
        assertTrue(vagasLivres(em(11, 10, 0), em(11, 11, 0)).contains(4));
        assertFalse(vagasLivres(em(11, 10, 0), em(11, 11, 0)).contains(3));
    }

    // A conferência do caso de uso passa, mas outro plano reserva a vaga no meio
    // do caminho (aqui simulado gravando a reserva direto). O UNIQUE do banco
    // derruba a gravação e a transação desfaz TUDO: a situação do plano e a
    // reserva de aerovia que já tinha sido gravada.
    @Test
    void conflitoNaGravacaoDesfazSituacaoEReservasJaGravadas() {
        PlanoDeVoo dono = planoPoaFln(em(12, 10, 0), 30_000, 3);
        aprovarPlano.executar(dono.getId());
        PlanoDeVoo disputa = planoPoaFln(em(12, 10, 0), 31_000, 3);

        Aerovia r1 = aerovias.buscarPorId("R1").orElseThrow();
        Aeroporto fln = aeroportos.buscarPorCodigo("FLN").orElseThrow();
        Slot slot = new Slot(em(12, 10, 0));
        ReservaAerovia livre = new ReservaAerovia(disputa.getId(), r1, 31_000, slot);
        ReservaVaga ocupada = new ReservaVaga(disputa.getId(), fln, 3, slot);

        assertThrows(ConflitoDeReservaException.class, () -> transacao.executar(() -> {
            disputa.aprovar(List.of(), em(1, 0, 0));
            planos.salvar(disputa);
            reservas.reservar(List.of(livre), List.of());
            reservas.reservar(List.of(), List.of(ocupada));
            return null;
        }));

        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(disputa.getId()).orElseThrow().getSituacao());
        assertTrue(altitudesLivres(em(12, 10, 0), em(12, 11, 0)).contains(31_000),
                "a reserva de aerovia gravada antes do conflito deveria ter sido desfeita");
        assertTrue(reservas.buscarReservasAerovia("R1", List.of(slot)).stream()
                .noneMatch(r -> r.getPlanoId().equals(disputa.getId())));
        // O plano dono continua íntegro.
        assertEquals(SituacaoPlano.APROVADO, planos.buscarPorId(dono.getId()).orElseThrow().getSituacao());
        assertFalse(altitudesLivres(em(12, 10, 0), em(12, 11, 0)).contains(30_000));
        assertFalse(vagasLivres(em(12, 10, 0), em(12, 11, 0)).contains(3));
    }

    @Test
    void mesmaAltitudeNoMesmoSlotNaoGravaDuasVezes() {
        PlanoDeVoo primeiro = planoPoaFln(em(13, 10, 0), 30_000, 3);
        aprovarPlano.executar(primeiro.getId());
        PlanoDeVoo segundo = planoPoaFln(em(13, 10, 0), 30_000, 4);
        ReservaAerovia repetida = new ReservaAerovia(segundo.getId(), aerovias.buscarPorId("R1").orElseThrow(),
                30_000, new Slot(em(13, 10, 0)));

        assertThrows(ConflitoDeReservaException.class,
                () -> transacao.executar(() -> {
                    reservas.reservar(List.of(repetida), List.of());
                    return null;
                }));
    }
}
