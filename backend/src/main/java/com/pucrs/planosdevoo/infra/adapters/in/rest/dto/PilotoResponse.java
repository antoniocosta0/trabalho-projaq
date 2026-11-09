package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.pucrs.planosdevoo.domain.model.Piloto;

public record PilotoResponse(Long id, String nome) {

    public static PilotoResponse de(Piloto p) {
        return new PilotoResponse(p.getId(), p.getNome());
    }
}
