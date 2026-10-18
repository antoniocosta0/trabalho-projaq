package com.pucrs.planosdevoo.infra.adapters.in.rest;

import java.time.LocalDateTime;
import java.util.List;

public record VagasDisponiveisResponseDTO(String aeroporto, LocalDateTime inicio, LocalDateTime fim,
                                          List<Integer> vagasDisponiveis) {
}
