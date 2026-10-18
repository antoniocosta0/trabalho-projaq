package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroportoRequestDTO(
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotNull Integer quantidadeVagas) {
}
