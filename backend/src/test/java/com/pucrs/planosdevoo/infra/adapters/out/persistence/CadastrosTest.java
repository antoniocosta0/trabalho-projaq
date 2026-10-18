package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AprovarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeronaveUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroportoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroviaUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarPilotoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.DefinirPercursoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// O teste de extensibilidade do enunciado (§2 e §8.1): aeroporto, aerovia,
// piloto e aeronave cadastrados em execução entram em uso num plano, sem
// reiniciar e sem recompilar.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:cadastros;DB_CLOSE_DELAY=-1")
class CadastrosTest {

    @Autowired CadastrarAeroportoUC cadastrarAeroporto;
    @Autowired CadastrarAeroviaUC cadastrarAerovia;
    @Autowired CadastrarPilotoUC cadastrarPiloto;
    @Autowired CadastrarAeronaveUC cadastrarAeronave;
    @Autowired CriarPlanoUC criarPlano;
    @Autowired DefinirPercursoUC definirPercurso;
    @Autowired AprovarPlanoUC aprovarPlano;
    @Autowired RepositorioAerovias aerovias;
    @Autowired RepositorioAeronaves aeronaves;

    @Test
    void cadastrosNovosEntramEmUsoNumPlanoNaMesmaExecucao() {
        cadastrarAeroporto.executar("SDU", "Santos Dumont - Rio de Janeiro", 12);
        cadastrarAerovia.executar("R9", "GRU", "SDU", 365);
        Piloto piloto = cadastrarPiloto.executar("Eduarda Nunes");
        cadastrarAeronave.executar(new DadosAeronave("PR-NEW", "PASSAGEIROS", 730, 4200, null, "Nova Linha",
                150, null));

        // 365 km a 730 km/h = 30 min.
        LocalDateTime partida = LocalDateTime.of(2031, 5, 2, 10, 0);
        PlanoDeVoo plano = criarPlano.executar(new CriarPlanoCommand(piloto.getId(), "PR-NEW", "GRU", "SDU", partida));
        definirPercurso.adicionarTrecho(plano.getId(), new TrechoCommand("R9", 30_000, partida, 12, 40));

        PlanoDeVoo aprovado = aprovarPlano.executar(plano.getId());

        assertEquals(SituacaoPlano.APROVADO, aprovado.getSituacao());
        assertEquals(LocalDateTime.of(2031, 5, 2, 10, 30), aprovado.getTrechos().get(0).horarioChegada());
    }

    @Test
    void pilotoNovoRecebeIdQueNaoColideComOsDaCargaInicial() {
        Piloto piloto = cadastrarPiloto.executar("Fábio Prates");

        assertTrue(piloto.getId() > 4, "id gerado: " + piloto.getId());
    }

    // Aerovia é imutável: POST com id existente é recusado e a original não muda.
    @Test
    void aeroviaComIdRepetidoERecusadaENaoAlteraAOriginal() {
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarAerovia.executar("R1", "POA", "GRU", 999));

        Aerovia r1 = aerovias.buscarPorId("R1").orElseThrow();
        assertEquals("FLN", r1.getDestino().getCodigo());
        assertEquals(363, r1.getDistanciaKm());
    }

    @Test
    void aeroviaComAeroportoInexistenteERecusada() {
        assertThrows(RecursoNaoEncontradoException.class, () -> cadastrarAerovia.executar("R90", "POA", "XXX", 100));
    }

    @Test
    void aeroportoEAeronaveRepetidosSaoRecusados() {
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarAeroporto.executar("POA", "Outro", 3));
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarAeronave.executar(
                new DadosAeronave("PP-PAR", "PARTICULAR", 500, 1000, "Oficina", null, null, null)));
    }

    @Test
    void aeronaveCadastradaVoltaDoBancoNaCategoriaCerta() {
        cadastrarAeronave.executar(new DadosAeronave("PR-CG2", "CARGA", 700, 3000, null, "Carga Sul", null, 12.5));

        Aeronave lida = aeronaves.buscarPorPrefixo("PR-CG2").orElseThrow();

        assertEquals("CARGA", lida.getCategoria());
        // Regra da categoria carga: fora da janela 00:00-06:00, não opera.
        assertFalse(lida.podeOperar(30_000, LocalDateTime.of(2031, 5, 3, 9, 0), LocalDateTime.of(2031, 5, 3, 9, 30)));
    }
}
