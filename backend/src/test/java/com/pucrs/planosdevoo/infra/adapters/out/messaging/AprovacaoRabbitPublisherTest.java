package com.pucrs.planosdevoo.infra.adapters.out.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.pucrs.planosdevoo.domain.fakes.PlanoDeVooRepositoryEmMemoria;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.OutboxEventoEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.OutboxEventoJpaRepository;
import java.net.ConnectException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.json.JsonMapper;

class AprovacaoRabbitPublisherTest {

    private static final LocalDateTime PARTIDA = LocalDateTime.of(2026, 10, 19, 10, 0);

    private RabbitTemplate rabbit;
    private AprovacaoRabbitPublisher publisher;
    private OutboxEventoEntity evento;

    @BeforeEach
    void preparar() {
        PlanoDeVooRepositoryEmMemoria planos = new PlanoDeVooRepositoryEmMemoria();
        PlanoDeVoo plano = PlanoDeVoo.reconstituir(1L, "ana", new Piloto(1L, "Ana"),
                new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180), "POA", "FLN", PARTIDA,
                SituacaoPlano.EM_EDICAO, List.of());
        plano.adicionarTrecho(new Aerovia("R1", "POA", "FLN", 363), 30_000, PARTIDA, new Pouso(3, 60));
        planos.adicionar(plano);
        rabbit = mock(RabbitTemplate.class);
        publisher = new AprovacaoRabbitPublisher(mock(OutboxEventoJpaRepository.class), planos, rabbit,
                JsonMapper.builder().build(), Clock.systemDefaultZone());
        evento = new OutboxEventoEntity("evento-1", 1L, PARTIDA.minusDays(1));
    }

    @Test
    void marcaComoPublicadoSoDepoisDaConfirmacao() {
        brokerResponde(true);

        publisher.publicar(evento);

        assertNotNull(evento.getPublicadoEm());
    }

    @Test
    void recusaDoBrokerMantemPendente() {
        brokerResponde(false);

        publisher.publicar(evento);

        assertNull(evento.getPublicadoEm());
        assertEquals(1, evento.getTentativas());
        assertNotNull(evento.getUltimoErro());
    }

    @Test
    void brokerForaDoArMantemPendente() {
        doThrow(new AmqpConnectException(new ConnectException("recusada")))
                .when(rabbit).send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));

        publisher.publicar(evento);

        assertNull(evento.getPublicadoEm());
        assertEquals(1, evento.getTentativas());
    }

    private void brokerResponde(boolean ack) {
        doAnswer(chamada -> {
            CorrelationData confirmacao = chamada.getArgument(3);
            confirmacao.getFuture().complete(new CorrelationData.Confirm(ack, ack ? null : "nack"));
            return null;
        }).when(rabbit).send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));
    }
}
