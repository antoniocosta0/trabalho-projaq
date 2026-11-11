package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.ports.out.MalhaAereaPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MalhaAereaEmMemoriaConfig {

    @Bean
    @Primary
    public MalhaAereaPort malhaAereaEmMemoria() {
        return new MalhaAereaEmMemoria();
    }
}
