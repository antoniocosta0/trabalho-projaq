package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.pucrs.planosdevoo.domain.ports.in.TrechoCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TrechoRequest(
        @NotBlank(message = "é obrigatório") String aeroviaId,
        @NotNull(message = "é obrigatório") Integer altitude,
        @NotNull(message = "é obrigatório") LocalDateTime horarioEntrada,
        Integer numeroVaga,
        Integer permanenciaMinutos) {

    public TrechoCommand paraComando() {
        return new TrechoCommand(aeroviaId, altitude, horarioEntrada, numeroVaga, permanenciaMinutos);
    }
}
