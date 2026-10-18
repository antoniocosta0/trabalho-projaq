package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroviaRequestDTO(
        @NotBlank String id,
        @NotBlank String origem,
        @NotBlank String destino,
        @NotNull Double distanciaKm) {
}
