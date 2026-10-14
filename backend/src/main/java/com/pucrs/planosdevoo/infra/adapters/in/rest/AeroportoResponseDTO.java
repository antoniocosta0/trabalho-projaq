package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeroporto;

public record AeroportoResponseDTO(String codigo, String nome, int quantidadeVagas) {

    public static AeroportoResponseDTO de(Aeroporto a) {
        return new AeroportoResponseDTO(a.getCodigo(), a.getNome(), a.getQuantidadeVagas());
    }
}
