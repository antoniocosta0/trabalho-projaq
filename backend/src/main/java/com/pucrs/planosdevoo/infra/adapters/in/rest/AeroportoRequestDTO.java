package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroportoRequestDTO(
        @NotBlank(message = "é obrigatório") String codigo,
        @NotBlank(message = "é obrigatório") String nome,
        @NotNull(message = "é obrigatório") Integer quantidadeVagas) {
}
