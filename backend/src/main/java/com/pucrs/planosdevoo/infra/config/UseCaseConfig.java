package com.pucrs.planosdevoo.infra.config;

import com.pucrs.planosdevoo.domain.model.VerificadorDeConflitos;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeronaveUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroportoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroviaUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarPilotoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AprovarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CancelarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAltitudesDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeronavesUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroportosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroviasUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPilotosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarVagasDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ListarPlanosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AbandonarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.DefinirPercursoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ValidarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPilotos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.Clock;
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
    public CadastrarAeroportoUC cadastrarAeroportoUC(RepositorioAeroportos aeroportos) {
        return new CadastrarAeroportoUC(aeroportos);
    }

    @Bean
    public CadastrarAeroviaUC cadastrarAeroviaUC(RepositorioAerovias aerovias, RepositorioAeroportos aeroportos) {
        return new CadastrarAeroviaUC(aerovias, aeroportos);
    }

    @Bean
    public CadastrarPilotoUC cadastrarPilotoUC(RepositorioPilotos pilotos) {
        return new CadastrarPilotoUC(pilotos);
    }

    @Bean
    public CadastrarAeronaveUC cadastrarAeronaveUC(RepositorioAeronaves aeronaves) {
        return new CadastrarAeronaveUC(aeronaves);
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

    @Bean
    public DefinirPercursoUC definirPercursoUC(RepositorioPlanosDeVoo planos, RepositorioAerovias aerovias) {
        return new DefinirPercursoUC(planos, aerovias);
    }

    @Bean
    public ValidarPlanoUC validarPlanoUC(RepositorioPlanosDeVoo planos, VerificadorDeConflitos verificador) {
        return new ValidarPlanoUC(planos, verificador);
    }

    @Bean
    public AbandonarPlanoUC abandonarPlanoUC(RepositorioPlanosDeVoo planos) {
        return new AbandonarPlanoUC(planos);
    }

    @Bean
    public VerificadorDeConflitos verificadorDeConflitos(RepositorioReservas reservas) {
        return new VerificadorDeConflitos(reservas);
    }

    @Bean
    public ConsultarAltitudesDisponiveisUC consultarAltitudesDisponiveisUC(RepositorioAerovias aerovias,
                                                                           RepositorioReservas reservas) {
        return new ConsultarAltitudesDisponiveisUC(aerovias, reservas);
    }

    @Bean
    public ConsultarVagasDisponiveisUC consultarVagasDisponiveisUC(RepositorioAeroportos aeroportos,
                                                                   RepositorioReservas reservas) {
        return new ConsultarVagasDisponiveisUC(aeroportos, reservas);
    }

    @Bean
    public AprovarPlanoUC aprovarPlanoUC(RepositorioPlanosDeVoo planos, RepositorioReservas reservas,
                                         VerificadorDeConflitos verificador, Transacao transacao) {
        return new AprovarPlanoUC(planos, reservas, verificador, transacao);
    }

    @Bean
    public CancelarPlanoUC cancelarPlanoUC(RepositorioPlanosDeVoo planos, RepositorioReservas reservas,
                                           Transacao transacao, Clock relogio) {
        return new CancelarPlanoUC(planos, reservas, transacao, relogio);
    }

    // "Agora" vem de um Clock injetado para o cancelamento ser testável com hora fixa.
    @Bean
    public Clock relogio() {
        return Clock.systemDefaultZone();
    }
}
