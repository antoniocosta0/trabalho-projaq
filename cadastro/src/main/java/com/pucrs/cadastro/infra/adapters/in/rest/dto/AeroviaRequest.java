package com.pucrs.cadastro.infra.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroviaRequest(
        @NotBlank(message = "é obrigatório") String id,
        @NotBlank(message = "é obrigatório") String origem,
        @NotBlank(message = "é obrigatório") String destino,
        @NotNull(message = "é obrigatório") Double distanciaKm) {
}
