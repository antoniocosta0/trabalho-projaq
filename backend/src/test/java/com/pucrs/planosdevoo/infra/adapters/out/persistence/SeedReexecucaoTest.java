package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// O data.sql roda a cada inicialização. Um plano do seed cancelado pela API
// precisa continuar cancelado depois de reiniciar, e sem as reservas de volta.
// Banco próprio: o teste cancela um plano e não pode atrapalhar os outros.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:reexecucao;DB_CLOSE_DELAY=-1")
class SeedReexecucaoTest {

    @Autowired RepositorioPlanosDeVoo planos;
    @Autowired RepositorioReservas reservas;
    @Autowired Transacao transacao;
    @Autowired DataSource dataSource;

    private static final List<Slot> SLOTS_DO_1003 = List.of(
            new Slot(LocalDateTime.of(2026, 10, 19, 21, 0)),
            new Slot(LocalDateTime.of(2026, 10, 19, 22, 0)));

    private void reexecutarCargaInicial() {
        new ResourceDatabasePopulator(new ClassPathResource("db/data.sql")).execute(dataSource);
    }

    @Test
    void reiniciarNaoDesfazCancelamentoDePlanoDoSeed() {
        PlanoDeVoo plano = planos.buscarPorId(1003L).orElseThrow();
        // "agora" fixo antes da partida, para o teste não depender do relógio.
        plano.cancelar(LocalDateTime.of(2026, 10, 18, 12, 0));
        transacao.executar(() -> {
            planos.salvar(plano);
            reservas.liberarReservasDoPlano(1003L);
            return null;
        });

        reexecutarCargaInicial();

        assertEquals(SituacaoPlano.CANCELADO, planos.buscarPorId(1003L).orElseThrow().getSituacao());
        assertTrue(reservas.buscarReservasAerovia("R2", SLOTS_DO_1003).stream()
                .noneMatch(r -> r.getPlanoId().equals(1003L)));
        assertTrue(reservas.buscarReservasVaga("GRU", SLOTS_DO_1003).stream()
                .noneMatch(r -> r.getPlanoId().equals(1003L)));
    }

    @Test
    void reexecutarNaoDuplicaNada() {
        int planosAntes = planos.listarTodos().size();

        reexecutarCargaInicial();

        assertEquals(planosAntes, planos.listarTodos().size());
        assertEquals(4, planos.buscarPorId(1001L).orElseThrow().getTrechos().size()
                + planos.buscarPorId(1002L).orElseThrow().getTrechos().size());
    }
}
