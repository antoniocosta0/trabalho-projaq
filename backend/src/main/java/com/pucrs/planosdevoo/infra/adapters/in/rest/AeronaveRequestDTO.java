package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Um corpo só para todas as categorias. Os campos próprios de cada uma são
// opcionais aqui: quem sabe quais são obrigatórios é a FabricaDeAeronaves.
public record AeronaveRequestDTO(
        @NotBlank String prefixo,
        @NotBlank String categoria,
        @NotNull Double velocidadeCruzeiroKmh,
        @NotNull Double autonomiaKm,
        String empresaManutencao,
        String companhiaAerea,
        Integer capacidadePassageiros,
        Double capacidadeToneladas) {

    public DadosAeronave paraDados() {
        return new DadosAeronave(prefixo, categoria, velocidadeCruzeiroKmh, autonomiaKm, empresaManutencao,
                companhiaAerea, capacidadePassageiros, capacidadeToneladas);
    }
}
