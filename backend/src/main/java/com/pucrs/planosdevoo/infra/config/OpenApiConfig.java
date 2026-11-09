package com.pucrs.planosdevoo.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Sistema de Gestão de Planos de Voo")
                .version("1.0.0")
                .description("Data de referência da carga inicial: 19/10/2026. Erros seguem o formato Problem Detail."));
    }
}
