package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CriarPlanoRequestDTO(
        @NotNull Long pilotoId,
        @NotBlank String aeronavePrefixo,
        @NotBlank String origem,
        @NotBlank String destino,
        @NotNull LocalDateTime partida) {
}
