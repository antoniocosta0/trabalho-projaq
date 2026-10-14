package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeronave;

public record AeronaveResponseDTO(String prefixo, String categoria, double velocidadeCruzeiroKmh, double autonomiaKm) {

    public static AeronaveResponseDTO de(Aeronave a) {
        return new AeronaveResponseDTO(a.getPrefixo(), a.getCategoria(), a.getVelocidadeCruzeiroKmh(),
                a.getAutonomiaKm());
    }
}
