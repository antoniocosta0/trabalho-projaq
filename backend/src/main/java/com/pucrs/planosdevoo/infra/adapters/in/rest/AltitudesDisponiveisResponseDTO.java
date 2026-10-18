package com.pucrs.planosdevoo.infra.adapters.in.rest;

import java.time.LocalDateTime;
import java.util.List;

public record AltitudesDisponiveisResponseDTO(String aeroviaId, LocalDateTime inicio, LocalDateTime fim,
                                              List<Integer> altitudesDisponiveis) {
}
