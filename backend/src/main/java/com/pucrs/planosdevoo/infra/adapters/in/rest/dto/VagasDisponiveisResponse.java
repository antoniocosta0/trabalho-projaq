package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VagasDisponiveisResponse(String aeroporto, LocalDateTime inicio, LocalDateTime fim,
                                          List<Integer> vagasDisponiveis) {
}
