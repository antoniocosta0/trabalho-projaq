package com.pucrs.cadastro.infra.adapters.in.rest.dto;

import com.pucrs.cadastro.domain.model.Aeroporto;

public record AeroportoResponse(String codigo, String nome, int quantidadeVagas) {

    public static AeroportoResponse de(Aeroporto a) {
        return new AeroportoResponse(a.getCodigo(), a.getNome(), a.getVagas().size());
    }
}
