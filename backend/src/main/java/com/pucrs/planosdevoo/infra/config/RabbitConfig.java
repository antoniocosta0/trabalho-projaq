package com.pucrs.planosdevoo.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// a mesma topologia é declarada pelo registro: quem subir primeiro cria, e a fila existe mesmo sem consumidor
@Configuration
@EnableScheduling
public class RabbitConfig {

    public static final String EXCHANGE = "planos.aprovacoes";
    public static final String ROTA = "plano.aprovado";
    public static final String FILA = "registro.aprovacoes";
    public static final String FILA_FALHAS = "registro.aprovacoes.dlq";

    @Bean
    public Declarables topologiaDeAprovacoes() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        Queue fila = QueueBuilder.durable(FILA)
                .deadLetterExchange("")
                .deadLetterRoutingKey(FILA_FALHAS)
                .build();
        Queue falhas = QueueBuilder.durable(FILA_FALHAS).build();
        Binding ligacao = BindingBuilder.bind(fila).to(exchange).with(ROTA);
        return new Declarables(exchange, fila, falhas, ligacao);
    }
}
