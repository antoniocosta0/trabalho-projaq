package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aerovia;

public record AeroviaResponseDTO(String id, String origem, String destino, double distanciaKm) {

    public static AeroviaResponseDTO de(Aerovia a) {
        return new AeroviaResponseDTO(a.getId(), a.getOrigem().getCodigo(), a.getDestino().getCodigo(),
                a.getDistanciaKm());
    }
}
