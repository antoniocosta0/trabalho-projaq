package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Piloto;

public record PilotoResponseDTO(Long id, String nome) {

    public static PilotoResponseDTO de(Piloto p) {
        return new PilotoResponseDTO(p.getId(), p.getNome());
    }
}
