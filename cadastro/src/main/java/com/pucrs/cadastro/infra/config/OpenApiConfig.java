package com.pucrs.cadastro.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Cadastro da rede aérea")
                .version("1.0.0")
                .description("Aeroportos, suas vagas e aerovias. Erros seguem o formato Problem Detail."));
    }
}
