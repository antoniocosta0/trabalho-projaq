package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.CadastrarAeronaveCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeronaveRequestDTO(
        @NotBlank(message = "é obrigatório") String prefixo,
        @NotBlank(message = "é obrigatório") String categoria,
        @NotNull(message = "é obrigatório") Double velocidadeCruzeiroKmh,
        @NotNull(message = "é obrigatório") Double autonomiaKm,
        String empresaManutencao,
        String companhiaAerea,
        Integer capacidadePassageiros,
        Double capacidadeToneladas) {

    public CadastrarAeronaveCommand paraComando() {
        return new CadastrarAeronaveCommand(prefixo, categoria, velocidadeCruzeiroKmh, autonomiaKm, empresaManutencao,
                companhiaAerea, capacidadePassageiros, capacidadeToneladas);
    }
}
