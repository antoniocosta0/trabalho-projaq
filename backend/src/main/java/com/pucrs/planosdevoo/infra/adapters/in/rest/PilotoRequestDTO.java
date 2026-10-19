package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;

public record PilotoRequestDTO(@NotBlank(message = "é obrigatório") String nome) {
}
