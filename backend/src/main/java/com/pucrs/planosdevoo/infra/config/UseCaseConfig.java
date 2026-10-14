package com.pucrs.planosdevoo.infra.config;

import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeronavesUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroportosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroviasUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPilotosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ListarPlanosUC;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPilotos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Os casos de uso não têm anotação do Spring. É aqui que eles viram beans
// e recebem os adaptadores de saída (o "Config" da estrutura do professor).
@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultarAeroportosUC consultarAeroportosUC(RepositorioAeroportos aeroportos) {
        return new ConsultarAeroportosUC(aeroportos);
    }

    @Bean
    public ConsultarAeroviasUC consultarAeroviasUC(RepositorioAerovias aerovias) {
        return new ConsultarAeroviasUC(aerovias);
    }

    @Bean
    public ConsultarPilotosUC consultarPilotosUC(RepositorioPilotos pilotos) {
        return new ConsultarPilotosUC(pilotos);
    }

    @Bean
    public ConsultarAeronavesUC consultarAeronavesUC(RepositorioAeronaves aeronaves) {
        return new ConsultarAeronavesUC(aeronaves);
    }

    @Bean
    public CriarPlanoUC criarPlanoUC(RepositorioPlanosDeVoo planos, RepositorioPilotos pilotos,
                                     RepositorioAeronaves aeronaves, RepositorioAeroportos aeroportos) {
        return new CriarPlanoUC(planos, pilotos, aeronaves, aeroportos);
    }

    @Bean
    public ConsultarPlanoUC consultarPlanoUC(RepositorioPlanosDeVoo planos) {
        return new ConsultarPlanoUC(planos);
    }

    @Bean
    public ListarPlanosUC listarPlanosUC(RepositorioPlanosDeVoo planos) {
        return new ListarPlanosUC(planos);
    }
}
