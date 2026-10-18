package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Confere o data.sql contra o próprio domínio: cada plano aprovado da carga
// inicial é válido, e as reservas gravadas são exatamente as que a aprovação
// teria gerado — nem uma a mais, nem uma a menos. Se alguém mudar uma linha do
// seed sem refazer as contas, o build quebra aqui, e não na apresentação.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:seed;DB_CLOSE_DELAY=-1")
class SeedConsistenciaTest {

    private static final LocalDate DATA_DE_REFERENCIA = LocalDate.of(2026, 10, 19);

    @Autowired RepositorioPlanosDeVoo planos;
    @Autowired RepositorioReservas reservas;
    @Autowired RepositorioAerovias aerovias;
    @Autowired RepositorioAeroportos aeroportos;
    @Autowired CriarPlanoUC criarPlano;

    // Do início da data de referência até o fim do dia seguinte: a permanência
    // no destino final pode passar da meia-noite.
    private static List<Slot> slotsDoPeriodo() {
        LocalDateTime inicio = DATA_DE_REFERENCIA.atStartOfDay();
        return IntStream.range(0, 48).mapToObj(h -> new Slot(inicio.plusHours(h))).toList();
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
            // validar() só roda em edição; uma cópia em edição confere as mesmas regras.
            PlanoDeVoo copia = PlanoDeVoo.reconstituir(p.getId(), p.getPiloto(), p.getAeronave(), p.getOrigem(),
                    p.getDestino(), p.getPartida(), SituacaoPlano.EM_EDICAO, p.getTrechos());
            assertEquals(List.of(), copia.validar(), "plano " + p.getId());
            assertEquals(DATA_DE_REFERENCIA, p.getPartida().toLocalDate(), "plano " + p.getId());
        }
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
        List<Slot> doDia = slotsDoPeriodo().subList(0, 24);
        List<Aerovia> todas = aerovias.listarTodas();
        long ocupadas = todas.stream()
                .filter(a -> !reservas.buscarReservasAerovia(a.getId(), doDia).isEmpty())
                .count();

        assertTrue(ocupadas >= Math.ceil(todas.size() * 0.6), ocupadas + " de " + todas.size());
    }

    // O roteiro docs/api/requests.http começa a contar os planos criados pela
    // API do 1; os do seed (1001+) não podem empurrar essa numeração.
    @Test
    void planoCriadoPelaApiNaoColideComOsIdsDoSeed() {
        PlanoDeVoo novo = criarPlano.executar(new CriarPlanoCommand(1L, "PR-PAX", "POA", "FLN",
                LocalDateTime.of(2031, 4, 1, 10, 0)));

        assertTrue(novo.getId() < 1001, "id gerado: " + novo.getId());
    }
}
