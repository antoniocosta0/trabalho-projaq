package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AeroportoRequest(
        @NotBlank(message = "é obrigatório") String codigo,
        @NotBlank(message = "é obrigatório") String nome,
        @NotNull(message = "é obrigatório") Integer quantidadeVagas) {
}
