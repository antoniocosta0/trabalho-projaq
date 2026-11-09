package com.pucrs.planosdevoo.infra.config;

import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import com.pucrs.planosdevoo.domain.ports.out.TransacaoPort;
import com.pucrs.planosdevoo.domain.services.DisponibilidadeService;
import com.pucrs.planosdevoo.domain.services.FrotaService;
import com.pucrs.planosdevoo.domain.services.MalhaAereaService;
import com.pucrs.planosdevoo.domain.services.PlanoDeVooService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public MalhaAereaService malhaAereaService(AeroportoRepository aeroportos, AeroviaRepository aerovias) {
        return new MalhaAereaService(aeroportos, aerovias);
    }

    @Bean
    public FrotaService frotaService(PilotoRepository pilotos, AeronaveRepository aeronaves) {
        return new FrotaService(pilotos, aeronaves);
    }

    @Bean
    public DisponibilidadeService disponibilidadeService(AeroviaRepository aerovias, AeroportoRepository aeroportos,
                                                         ReservaRepository reservas) {
        return new DisponibilidadeService(aerovias, aeroportos, reservas);
    }

    @Bean
    public PlanoDeVooService planoDeVooService(PlanoDeVooRepository planos, PilotoRepository pilotos,
                                               AeronaveRepository aeronaves, AeroportoRepository aeroportos,
                                               AeroviaRepository aerovias, ReservaRepository reservas,
                                               DisponibilidadeService disponibilidade, TransacaoPort transacao,
                                               Clock relogio) {
        return new PlanoDeVooService(planos, pilotos, aeronaves, aeroportos, aerovias, reservas, disponibilidade,
                transacao, relogio);
    }

    // app.relogio.instante-fixo permite ensaiar a apresentação em outro dia
    @Bean
    public Clock relogio(@Value("${app.relogio.zona:America/Sao_Paulo}") String zona,
                         @Value("${app.relogio.instante-fixo:}") String instanteFixo) {
        ZoneId fuso = ZoneId.of(zona);
        if (instanteFixo.isBlank()) {
            return Clock.system(fuso);
        }
        return Clock.fixed(LocalDateTime.parse(instanteFixo).atZone(fuso).toInstant(), fuso);
    }
}
