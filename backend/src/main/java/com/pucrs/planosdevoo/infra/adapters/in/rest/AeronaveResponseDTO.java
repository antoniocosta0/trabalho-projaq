package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;

// Os campos que não se aplicam à categoria ficam de fora da resposta, em vez de
// aparecerem como null: aeronave particular não tem companhia aérea.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AeronaveResponseDTO(String prefixo, String categoria, double velocidadeCruzeiroKmh,
                                  double autonomiaKm, String empresaManutencao, String companhiaAerea,
                                  Integer capacidadePassageiros, Double capacidadeToneladas) {

    // Quem diz quais campos a aeronave tem é ela mesma, por polimorfismo.
    public static AeronaveResponseDTO de(Aeronave a) {
        DadosAeronave d = a.dados();
        return new AeronaveResponseDTO(d.prefixo(), d.categoria(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                d.empresaManutencao(), d.companhiaAerea(), d.capacidadePassageiros(), d.capacidadeToneladas());
    }
}
