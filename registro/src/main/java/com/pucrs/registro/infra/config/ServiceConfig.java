package com.pucrs.registro.infra.config;

import com.pucrs.registro.domain.ports.out.RegistroAprovacaoRepository;
import com.pucrs.registro.domain.services.RegistroAprovacaoService;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public RegistroAprovacaoService registroAprovacaoService(RegistroAprovacaoRepository registros) {
        return new RegistroAprovacaoService(registros);
    }

    @Bean
    public Clock relogio(@Value("${app.relogio.zona:America/Sao_Paulo}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }
}
