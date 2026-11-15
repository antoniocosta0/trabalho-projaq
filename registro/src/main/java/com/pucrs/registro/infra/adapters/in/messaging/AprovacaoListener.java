package com.pucrs.registro.infra.adapters.in.messaging;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import com.pucrs.registro.domain.ports.in.RegistroAprovacaoUseCase;
import com.pucrs.registro.infra.config.RabbitConfig;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

// o ack só sai quando este método termina sem exceção, isto é, depois da gravação
@Component
public class AprovacaoListener {

    private static final Logger log = LoggerFactory.getLogger(AprovacaoListener.class);

    private final RegistroAprovacaoUseCase registros;
    private final JsonMapper json;
    private final Clock relogio;

    public AprovacaoListener(RegistroAprovacaoUseCase registros, JsonMapper json, Clock relogio) {
        this.registros = registros;
        this.json = json;
        this.relogio = relogio;
    }

    @RabbitListener(queues = RabbitConfig.FILA)
    public void receber(Message mensagem) {
        String conteudo = new String(mensagem.getBody(), StandardCharsets.UTF_8);
        JsonNode aprovacao = json.readTree(conteudo);
        RegistroAprovacao registro = new RegistroAprovacao(
                aprovacao.required("eventoId").asString(),
                aprovacao.required("plano").required("id").asLong(),
                LocalDateTime.parse(aprovacao.required("aprovadoEm").asString()),
                LocalDateTime.now(relogio),
                conteudo);
        boolean novo = registros.registrar(registro);
        log.info("Aprovação do plano {} {} (evento {})", registro.getPlanoId(),
                novo ? "registrada" : "já registrada, ignorada", registro.getEventoId());
    }
}
