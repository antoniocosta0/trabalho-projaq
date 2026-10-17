package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// Para pouso, informe numeroVaga e permanenciaMinutos. Sem os dois, é sobrevoo.
public record TrechoRequestDTO(
        @NotBlank String aeroviaId,
        @NotNull Integer altitude,
        @NotNull LocalDateTime horarioEntrada,
        Integer numeroVaga,
        Integer permanenciaMinutos) {
}
