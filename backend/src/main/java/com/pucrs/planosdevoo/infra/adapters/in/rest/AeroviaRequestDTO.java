package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroviaRequestDTO(
        @NotBlank(message = "é obrigatório") String id,
        @NotBlank(message = "é obrigatório") String origem,
        @NotBlank(message = "é obrigatório") String destino,
        @NotNull(message = "é obrigatório") Double distanciaKm) {
}
