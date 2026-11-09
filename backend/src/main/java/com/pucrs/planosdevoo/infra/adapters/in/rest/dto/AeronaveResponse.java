package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronaveCarga;
import com.pucrs.planosdevoo.domain.model.AeronaveParticular;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AeronaveResponse(String prefixo, String categoria, double velocidadeCruzeiroKmh,
                                  double autonomiaKm, String empresaManutencao, String companhiaAerea,
                                  Integer capacidadePassageiros, Double capacidadeToneladas) {

    public static AeronaveResponse de(Aeronave a) {
        return switch (a) {
            case AeronaveParticular p -> de(p, p.getEmpresaManutencao(), null, null, null);
            case AeronavePassageiros p -> de(p, null, p.getCompanhiaAerea(), p.getCapacidadePassageiros(), null);
            case AeronaveCarga c -> de(c, null, c.getCompanhiaAerea(), null, c.getCapacidadeToneladas());
        };
    }

    private static AeronaveResponse de(Aeronave a, String empresaManutencao, String companhiaAerea,
                                          Integer capacidadePassageiros, Double capacidadeToneladas) {
        return new AeronaveResponse(a.getPrefixo(), a.getCategoria(), a.getVelocidadeCruzeiroKmh(),
                a.getAutonomiaKm(), empresaManutencao, companhiaAerea, capacidadePassageiros, capacidadeToneladas);
    }
}
