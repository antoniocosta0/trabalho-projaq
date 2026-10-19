package com.pucrs.planosdevoo.infra.adapters.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CriarPlanoRequestDTO(
        @NotNull(message = "é obrigatório") Long pilotoId,
        @NotBlank(message = "é obrigatório") String aeronavePrefixo,
        @NotBlank(message = "é obrigatório") String origem,
        @NotBlank(message = "é obrigatório") String destino,
        @NotNull(message = "é obrigatório") LocalDateTime partida) {
}
