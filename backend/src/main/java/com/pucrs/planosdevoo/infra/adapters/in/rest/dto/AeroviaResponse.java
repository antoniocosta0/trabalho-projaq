package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.pucrs.planosdevoo.domain.model.Aerovia;

public record AeroviaResponse(String id, String origem, String destino, double distanciaKm) {

    public static AeroviaResponse de(Aerovia a) {
        return new AeroviaResponse(a.getId(), a.getOrigem().getCodigo(), a.getDestino().getCodigo(),
                a.getDistanciaKm());
    }
}
