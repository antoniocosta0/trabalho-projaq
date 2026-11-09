package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.pucrs.planosdevoo.domain.ports.in.CriarPlanoCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CriarPlanoRequest(
        @NotNull(message = "é obrigatório") Long pilotoId,
        @NotBlank(message = "é obrigatório") String aeronavePrefixo,
        @NotBlank(message = "é obrigatório") String origem,
        @NotBlank(message = "é obrigatório") String destino,
        @NotNull(message = "é obrigatório") LocalDateTime partida) {

    public CriarPlanoCommand paraComando() {
        return new CriarPlanoCommand(pilotoId, aeronavePrefixo, origem, destino, partida);
    }
}
