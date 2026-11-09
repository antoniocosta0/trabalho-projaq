package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record PilotoRequest(@NotBlank(message = "é obrigatório") String nome) {
}
