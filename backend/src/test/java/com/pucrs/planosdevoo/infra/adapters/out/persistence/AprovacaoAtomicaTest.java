package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.planosdevoo.domain.exceptions.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.in.PlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.PlanoDeVooUseCase;
import com.pucrs.planosdevoo.domain.ports.in.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import com.pucrs.planosdevoo.domain.ports.out.TransacaoPort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:atomicidade;DB_CLOSE_DELAY=-1")
class AprovacaoAtomicaTest {

    private static final LocalDateTime PARTIDA = LocalDateTime.of(2031, 3, 12, 10, 0);

    @Autowired PlanoDeVooUseCase planoDeVoo;
    @Autowired PlanoDeVooRepository planos;
    @Autowired ReservaRepository reservas;
    @Autowired AeroviaRepository aerovias;
    @Autowired AeroportoRepository aeroportos;
    @Autowired TransacaoPort transacao;

    @Test
    void falhaAoReservarDesfazASituacaoEAsReservasJaGravadas() {
        planoDeVoo.aprovar(planoPoaFln(30_000, 3).getId());
        PlanoDeVoo disputa = planoPoaFln(31_000, 3);
        Slot slot = new Slot(PARTIDA);
        ReservaAerovia altitudeLivre = new ReservaAerovia(disputa.getId(), aerovias.buscarPorId("R1").orElseThrow(),
                31_000, slot);
        ReservaVaga vagaOcupada = new ReservaVaga(disputa.getId(), aeroportos.buscarPorCodigo("FLN").orElseThrow(),
                3, slot);

        assertThrows(ConflitoDeReservaException.class, () -> transacao.executar(() -> {
            disputa.aprovar(List.of());
            planos.salvar(disputa);
            reservas.reservar(List.of(altitudeLivre), List.of(vagaOcupada));
            return null;
        }));

        assertEquals(SituacaoPlano.EM_EDICAO, planos.buscarPorId(disputa.getId()).orElseThrow().getSituacao());
        assertTrue(reservas.buscarReservasAerovia("R1", List.of(slot)).stream()
                .noneMatch(r -> r.getPlanoId().equals(disputa.getId())));
    }

    private PlanoDeVoo planoPoaFln(int altitude, int vaga) {
        PlanoDeVoo plano = planoDeVoo.criar(new PlanoCommand(1L, "PR-PAX", "POA", "FLN", PARTIDA));
        return planoDeVoo.adicionarTrecho(plano.getId(), new TrechoCommand("R1", altitude, PARTIDA, vaga, 20));
    }
}
