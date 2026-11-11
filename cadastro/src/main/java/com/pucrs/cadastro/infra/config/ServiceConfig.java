package com.pucrs.cadastro.infra.config;

import com.pucrs.cadastro.domain.ports.out.AeroportoRepository;
import com.pucrs.cadastro.domain.ports.out.AeroviaRepository;
import com.pucrs.cadastro.domain.services.MalhaAereaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public MalhaAereaService malhaAereaService(AeroportoRepository aeroportos, AeroviaRepository aerovias) {
        return new MalhaAereaService(aeroportos, aerovias);
    }
}
