package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:seed;DB_CLOSE_DELAY=-1")
class SeedTest {

    private static final LocalDate DATA_DE_REFERENCIA = LocalDate.of(2026, 10, 19);

    @Autowired PlanoDeVooRepository planos;
    @Autowired ReservaRepository reservas;
    @Autowired AeroviaRepository aerovias;

    @Test
    void peloMenos60PorCentoDasAeroviasTemOcupacaoNaDataDeReferencia() {
        List<Aerovia> todas = aerovias.listarTodas();
        long ocupadas = todas.stream()
                .filter(a -> !reservas.buscarReservasAerovia(a.getId(), slotsDaDataDeReferencia()).isEmpty())
                .count();

        assertTrue(ocupadas >= Math.ceil(todas.size() * 0.6), ocupadas + " de " + todas.size());
    }

    @Test
    void ocupacaoDaDataDeReferenciaVemDosPlanosAprovados() {
        long esperadas = aprovados().stream()
                .flatMap(p -> p.reservasDeAerovia().stream())
                .filter(r -> r.getSlot().getInicio().toLocalDate().equals(DATA_DE_REFERENCIA))
                .count();
        long noBanco = aerovias.listarTodas().stream()
                .mapToLong(a -> reservas.buscarReservasAerovia(a.getId(), slotsDaDataDeReferencia()).size())
                .sum();

        assertEquals(esperadas, noBanco);
    }

    @Test
    void existePlanoAprovadoJaPartidoParaDemonstrarOCancelamentoRecusado() {
        assertTrue(aprovados().stream().anyMatch(p -> p.getPartida().toLocalDate().isBefore(DATA_DE_REFERENCIA)));
    }

    private List<PlanoDeVoo> aprovados() {
        return LongStream.rangeClosed(1001, 1005).mapToObj(id -> planos.buscarPorId(id).orElseThrow()).toList();
    }

    private static List<Slot> slotsDaDataDeReferencia() {
        return IntStream.range(0, 24).mapToObj(h -> new Slot(DATA_DE_REFERENCIA.atTime(h, 0))).toList();
    }
}
