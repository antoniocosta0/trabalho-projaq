package com.pucrs.planosdevoo.infra.adapters.out.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronaveCarga;
import com.pucrs.planosdevoo.domain.model.AeronaveParticular;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Trecho;
import java.time.LocalDateTime;
import java.util.List;

// contrato documentado em docs/api/mensagens.md
public record AprovacaoMensagem(String eventoId, int versao, LocalDateTime aprovadoEm, PlanoAprovado plano) {

    public static final int VERSAO = 1;

    public static AprovacaoMensagem de(String eventoId, LocalDateTime aprovadoEm, PlanoDeVoo p) {
        return new AprovacaoMensagem(eventoId, VERSAO, aprovadoEm, new PlanoAprovado(p.getId(), p.getCriador(),
                new PilotoDoPlano(p.getPiloto().getId(), p.getPiloto().getNome()),
                AeronaveDoPlano.de(p.getAeronave()),
                p.getOrigem(), p.getDestino(), p.getPartida(), p.getTrechos().stream().map(TrechoDoPlano::de).toList()));
    }

    public record PlanoAprovado(Long id, String criador, PilotoDoPlano piloto, AeronaveDoPlano aeronave,
                                String origem, String destino, LocalDateTime partida, List<TrechoDoPlano> trechos) { }

    public record PilotoDoPlano(Long id, String nome) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AeronaveDoPlano(String prefixo, String categoria, double velocidadeCruzeiroKmh, double autonomiaKm,
                                  String empresaManutencao, String companhiaAerea, Integer capacidadePassageiros,
                                  Double capacidadeToneladas) {

        static AeronaveDoPlano de(Aeronave a) {
            return switch (a) {
                case AeronaveParticular p -> de(p, p.getEmpresaManutencao(), null, null, null);
                case AeronavePassageiros p -> de(p, null, p.getCompanhiaAerea(), p.getCapacidadePassageiros(), null);
                case AeronaveCarga c -> de(c, null, c.getCompanhiaAerea(), null, c.getCapacidadeToneladas());
            };
        }

        private static AeronaveDoPlano de(Aeronave a, String empresaManutencao, String companhiaAerea,
                                          Integer capacidadePassageiros, Double capacidadeToneladas) {
            return new AeronaveDoPlano(a.getPrefixo(), a.getCategoria(), a.getVelocidadeCruzeiroKmh(),
                    a.getAutonomiaKm(), empresaManutencao, companhiaAerea, capacidadePassageiros, capacidadeToneladas);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TrechoDoPlano(int ordem, String aerovia, String origem, String destino, int altitude,
                                LocalDateTime entrada, LocalDateTime chegada, String noDestino, Integer numeroVaga,
                                Integer permanenciaMinutos, LocalDateTime saidaDaVaga) {

        static TrechoDoPlano de(Trecho t) {
            boolean pousa = t.getPouso() != null;
            return new TrechoDoPlano(t.getOrdem(), t.getAerovia().getId(), t.getAerovia().getOrigem(),
                    t.getAerovia().getDestino(), t.getAltitude(), t.getHorarioEntrada(), t.horarioChegada(),
                    pousa ? "POUSO" : "SOBREVOO",
                    pousa ? t.getPouso().getNumeroVaga() : null,
                    pousa ? t.getPouso().getPermanenciaMinutos() : null,
                    pousa ? t.getPouso().saida(t.horarioChegada()) : null);
        }
    }
}
