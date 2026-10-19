package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AprovarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAltitudesDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.DefinirPercursoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Duas aprovações ao mesmo tempo, disputando a MESMA altitude no MESMO slot.
//
// O AprovacaoAtomicaTest cobre o rollback forçando o conflito dentro da
// transação, de uma thread só. Este cobre a corrida de verdade.
//
// O que se garante aqui é o RESULTADO, não qual das duas defesas agiu: a
// segunda aprovação pode ser barrada pela conferência prévia (PlanoInvalido,
// 422) ou pelo UNIQUE do banco (ConflitoDeReserva, 409), conforme o momento em
// que as threads se cruzarem. Nos dois caminhos o esperado é o mesmo — uma
// aprovação, uma reserva, e nenhuma sobra no plano recusado.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:concorrencia;DB_CLOSE_DELAY=-1")
class AprovacaoConcorrenteTest {

    private static final int ALTITUDE_DISPUTADA = 33_000;

    @Autowired CriarPlanoUC criarPlano;
    @Autowired DefinirPercursoUC definirPercurso;
    @Autowired AprovarPlanoUC aprovarPlano;
    @Autowired ConsultarAltitudesDisponiveisUC consultarAltitudes;
    @Autowired RepositorioPlanosDeVoo planos;
    @Autowired RepositorioReservas reservas;

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2031, 5, 20, hora, minuto);
    }

    private Long planoDisputando() {
        PlanoDeVoo plano = criarPlano.executar(new CriarPlanoCommand(1L, "PR-PAX", "POA", "FLN", em(9, 0)));
        definirPercurso.adicionarTrecho(plano.getId(),
                new TrechoCommand("R1", ALTITUDE_DISPUTADA, em(9, 0), 5, 20));
        return plano.getId();
    }

    @Test
    void duasAprovacoesSimultaneasNaMesmaAltitudeESlotDeixamExatamenteUmaAprovada() throws Exception {
        Long primeiro = planoDisputando();
        Long segundo = planoDisputando();

        // As duas threads só partem depois que as duas estiverem prontas, para a
        // disputa ser real e não uma execução em sequência disfarçada.
        CountDownLatch largada = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<String> umaDelas = executor.submit(() -> tentar(primeiro, largada));
            Future<String> outra = executor.submit(() -> tentar(segundo, largada));
            largada.countDown();

            String resultadoDoPrimeiro = umaDelas.get(30, TimeUnit.SECONDS);
            String resultadoDoSegundo = outra.get(30, TimeUnit.SECONDS);

            long aprovadas = List.of(resultadoDoPrimeiro, resultadoDoSegundo).stream()
                    .filter("APROVADO"::equals).count();
            assertEquals(1, aprovadas,
                    "esperado exatamente um aprovado, veio: " + resultadoDoPrimeiro + " e " + resultadoDoSegundo);
        } finally {
            executor.shutdownNow();
        }

        // O recurso disputado saiu da lista de disponíveis uma única vez...
        List<Integer> livres = consultarAltitudes.executar("R1", em(9, 0), em(10, 0));
        assertTrue(livres.stream().noneMatch(a -> a == ALTITUDE_DISPUTADA));

        // ...e existe uma, e só uma, reserva naquele slot e altitude.
        long reservasDoSlot = reservas.buscarReservasAerovia("R1", List.of(new Slot(em(9, 0)))).stream()
                .filter(r -> r.getAltitude() == ALTITUDE_DISPUTADA)
                .count();
        assertEquals(1, reservasDoSlot, "só pode existir uma reserva para a mesma altitude no mesmo slot");

        // O plano que perdeu continua em edição, sem reserva parcial.
        long emEdicao = List.of(primeiro, segundo).stream()
                .map(id -> planos.buscarPorId(id).orElseThrow())
                .filter(p -> p.getSituacao() == SituacaoPlano.EM_EDICAO)
                .count();
        assertEquals(1, emEdicao, "o plano recusado deveria ter ficado EM_EDICAO");
    }

    // Devolve a situação final, ou o nome da exceção que recusou a aprovação.
    private String tentar(Long planoId, CountDownLatch largada) {
        try {
            largada.await();
            return aprovarPlano.executar(planoId).getSituacao().name();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "INTERROMPIDO";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }
}
