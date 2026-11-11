package com.pucrs.cadastro.infra.adapters.in.rest.dto;

import com.pucrs.cadastro.domain.model.Aeroporto;
import java.util.List;

public record VagasResponse(String aeroporto, List<Integer> vagas) {

    public static VagasResponse de(Aeroporto a) {
        return new VagasResponse(a.getCodigo(), a.getVagas());
    }
}
