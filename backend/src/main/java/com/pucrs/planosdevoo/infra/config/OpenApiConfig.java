package com.pucrs.planosdevoo.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Metadados do documento OpenAPI. As operações em si são descritas nos
// controllers, com @Operation e @ApiResponse.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestão de Planos de Voo")
                        .version("1.0.0")
                        .description("""
                                API REST do Trabalho Final (parte 1) de Projeto e Arquitetura de Software \
                                — PUCRS, turma 340, 2026/2.

                                O sistema monta um plano de voo trecho a trecho (aerovia, altitude, horário \
                                de entrada e o que acontece no aeroporto de destino do trecho), **verifica** \
                                se o percurso é contínuo, se os horários batem com a duração calculada, se a \
                                aeronave pode operar naquela altitude e horário, se a autonomia aguenta e se \
                                altitude e vaga estão livres — e então **reserva tudo de uma vez** e aprova.

                                ### Data de referência: 19/10/2026

                                É a data dos planos que a carga inicial já traz aprovados. As reservas deles \
                                ocupam 6 das 8 aerovias nesse dia; a R1 e a R3 ficam livres de propósito, \
                                para sempre haver recurso disponível numa demonstração.

                                ### Conceitos que explicam as respostas

                                - **Slot**: a ocupação é controlada em faixas de uma hora alinhadas à hora \
                                cheia, no intervalo `[início, fim)`. Qualquer uso parcial ocupa o slot \
                                inteiro: das 15:52 às 17:07 são três slots.
                                - **Validar não reserva**: `POST /planos/{id}/validacao` devolve a lista de \
                                problemas sem alterar o plano e sem criar reserva nenhuma.
                                - **Aprovação é integral**: ou reserva todos os recursos e aprova, ou não \
                                deixa reserva nenhuma e o plano continua em edição.
                                - **Duração**: distância da aerovia ÷ velocidade de cruzeiro, arredondada ao \
                                minuto. O horário de chegada é sempre calculado pelo sistema.

                                ### Erros

                                Toda resposta de erro segue o formato **Problem Detail** (RFC 7807). Quando \
                                há mais de um problema, eles vêm na propriedade `problemas`, um por item.""")
                        .contact(new Contact().name("Grupo TF_P1 6 — turma 340")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Execução local")));
    }
}
