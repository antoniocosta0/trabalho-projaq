package com.pucrs.planosdevoo.domain.ports.in;

import java.time.LocalDateTime;

public record TrechoCommand(String aeroviaId, int altitude, LocalDateTime horarioEntrada,
                            Integer numeroVaga, Integer permanenciaMinutos) {
}
