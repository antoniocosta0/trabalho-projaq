package com.pucrs.planosdevoo.infra.adapters.out.messaging;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.OutboxEventoEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.OutboxEventoJpaRepository;
import com.pucrs.planosdevoo.infra.config.RabbitConfig;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

// lê o outbox e só marca como publicado o que o broker confirmou; o resto fica para a próxima rodada
@Component
@ConditionalOnProperty(name = "app.outbox.publicar", havingValue = "true", matchIfMissing = true)
public class AprovacaoRabbitPublisher {

    private static final Logger log = LoggerFactory.getLogger(AprovacaoRabbitPublisher.class);

    private final OutboxEventoJpaRepository outbox;
    private final PlanoDeVooRepository planos;
    private final RabbitTemplate rabbit;
    private final JsonMapper json;
    private final Clock relogio;

    public AprovacaoRabbitPublisher(OutboxEventoJpaRepository outbox, PlanoDeVooRepository planos,
                                    RabbitTemplate rabbit, JsonMapper json, Clock relogio) {
        this.outbox = outbox;
        this.planos = planos;
        this.rabbit = rabbit;
        this.json = json;
        this.relogio = relogio;
    }

    @Scheduled(fixedDelayString = "${app.outbox.intervalo:5s}")
    public void publicarPendentes() {
        outbox.findByPublicadoEmIsNullOrderByAprovadoEm().forEach(this::publicar);
    }

    void publicar(OutboxEventoEntity evento) {
        try {
            enviar(evento);
            evento.marcarPublicado(LocalDateTime.now(relogio));
            log.info("Aprovação do plano {} confirmada pelo broker (evento {})", evento.getPlanoId(),
                    evento.getEventoId());
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            evento.registrarFalha(e.getMessage());
            log.warn("Aprovação do plano {} não publicada (tentativa {}): {}", evento.getPlanoId(),
                    evento.getTentativas(), e.getMessage());
        }
        outbox.save(evento);
    }

    private void enviar(OutboxEventoEntity evento) throws Exception {
        PlanoDeVoo plano = planos.buscarPorId(evento.getPlanoId()).orElseThrow();
        AprovacaoMensagem conteudo = AprovacaoMensagem.de(evento.getEventoId(), evento.getAprovadoEm(), plano);
        Message mensagem = MessageBuilder.withBody(json.writeValueAsBytes(conteudo))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setMessageId(evento.getEventoId())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        CorrelationData confirmacao = new CorrelationData(evento.getEventoId());
        rabbit.send(RabbitConfig.EXCHANGE, RabbitConfig.ROTA, mensagem, confirmacao);
        CorrelationData.Confirm resposta = confirmacao.getFuture().get(5, TimeUnit.SECONDS);
        if (!resposta.ack() || confirmacao.getReturned() != null) {
            throw new IllegalStateException("o broker não confirmou a mensagem: " + resposta.reason());
        }
    }
}
