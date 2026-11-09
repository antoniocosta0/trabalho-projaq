package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.in.PlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.PlanoDeVooUseCase;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:seed;DB_CLOSE_DELAY=-1")
class SeedConsistenciaTest {

    private static final LocalDate DATA_DE_REFERENCIA = LocalDate.of(2026, 10, 19);

    @Autowired PlanoDeVooRepository planos;
    @Autowired ReservaRepository reservas;
    @Autowired AeroviaRepository aerovias;
    @Autowired AeroportoRepository aeroportos;
    @Autowired PlanoDeVooUseCase planoDeVoo;

    private static List<Slot> slotsDoPeriodo() {
        LocalDateTime inicio = DATA_DE_REFERENCIA.minusDays(2).atStartOfDay();
        return IntStream.range(0, 96).mapToObj(h -> new Slot(inicio.plusHours(h))).toList();
    }

    private static List<Slot> slotsDaDataDeReferencia() {
        LocalDateTime inicio = DATA_DE_REFERENCIA.atStartOfDay();
        return IntStream.range(0, 24).mapToObj(h -> new Slot(inicio.plusHours(h))).toList();
    }

    private List<PlanoDeVoo> aprovados() {
        return planos.listarPorSituacao(SituacaoPlano.APROVADO);
    }

    private Set<String> reservasDeAeroviaNoBanco() {
        return aerovias.listarTodas().stream()
                .flatMap(a -> reservas.buscarReservasAerovia(a.getId(), slotsDoPeriodo()).stream())
                .map(SeedConsistenciaTest::chave)
                .collect(Collectors.toSet());
    }

    private Set<String> reservasDeVagaNoBanco() {
        return aeroportos.listarTodos().stream()
                .flatMap(a -> reservas.buscarReservasVaga(a.getCodigo(), slotsDoPeriodo()).stream())
                .map(SeedConsistenciaTest::chave)
                .collect(Collectors.toSet());
    }

    private static String chave(ReservaAerovia r) {
        return r.getPlanoId() + "|" + r.getAerovia().getId() + "|" + r.getAltitude() + "|" + r.getSlot().getInicio();
    }

    private static String chave(ReservaVaga r) {
        return r.getPlanoId() + "|" + r.getAeroporto().getCodigo() + "|" + r.getNumeroVaga() + "|"
                + r.getSlot().getInicio();
    }

    @Test
    void todoPlanoAprovadoDoSeedPassaNaValidacao() {
        assertFalse(aprovados().isEmpty(), "a carga inicial precisa ter planos aprovados");
        for (PlanoDeVoo p : aprovados()) {
            PlanoDeVoo copia = PlanoDeVoo.reconstituir(p.getId(), p.getPiloto(), p.getAeronave(), p.getOrigem(),
                    p.getDestino(), p.getPartida(), SituacaoPlano.EM_EDICAO, p.getTrechos());
            assertEquals(List.of(), copia.validar(), "plano " + p.getId());
            assertFalse(p.getPartida().toLocalDate().isAfter(DATA_DE_REFERENCIA), "plano " + p.getId());
        }
    }

    @Test
    void aCargaTemPlanoAprovadoComPartidaJaOcorrida() {
        assertTrue(aprovados().stream().anyMatch(p -> p.getPartida().toLocalDate().isBefore(DATA_DE_REFERENCIA)),
                "nenhum plano aprovado da carga inicial tem partida anterior à data de referência");
    }

    @Test
    void reservasSaoExatamenteAsQueAAprovacaoGeraria() {
        Set<String> esperadasAerovia = new HashSet<>();
        Set<String> esperadasVaga = new HashSet<>();
        for (PlanoDeVoo p : aprovados()) {
            p.reservasDeAerovia().forEach(r -> esperadasAerovia.add(chave(r)));
            p.reservasDeVaga().forEach(r -> esperadasVaga.add(chave(r)));
        }

        assertEquals(esperadasAerovia, reservasDeAeroviaNoBanco());
        assertEquals(esperadasVaga, reservasDeVagaNoBanco());
    }

    @Test
    void peloMenos60PorCentoDasAeroviasTemOcupacaoNaDataDeReferencia() {
        List<Slot> doDia = slotsDaDataDeReferencia();
        List<Aerovia> todas = aerovias.listarTodas();
        long ocupadas = todas.stream()
                .filter(a -> !reservas.buscarReservasAerovia(a.getId(), doDia).isEmpty())
                .count();

        assertTrue(ocupadas >= Math.ceil(todas.size() * 0.6), ocupadas + " de " + todas.size());
    }

    @Test
    void planoCriadoPelaApiNaoColideComOsIdsDoSeed() {
        PlanoDeVoo novo = planoDeVoo.criar(new PlanoCommand(1L, "PR-PAX", "POA", "FLN",
                LocalDateTime.of(2031, 4, 1, 10, 0)));

        assertTrue(novo.getId() < 1001, "id gerado: " + novo.getId());
    }
}
