package com.pucrs.cadastro.infra.adapters.in.rest.dto;

import com.pucrs.cadastro.domain.model.Aerovia;

public record AeroviaResponse(String id, String origem, String destino, double distanciaKm) {

    public static AeroviaResponse de(Aerovia a) {
        return new AeroviaResponse(a.getId(), a.getOrigem(), a.getDestino(), a.getDistanciaKm());
    }
}
