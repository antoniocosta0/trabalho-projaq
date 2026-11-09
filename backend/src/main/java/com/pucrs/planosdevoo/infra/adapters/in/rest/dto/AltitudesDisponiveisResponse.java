package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AltitudesDisponiveisResponse(String aeroviaId, LocalDateTime inicio, LocalDateTime fim,
                                              List<Integer> altitudesDisponiveis) {
}
