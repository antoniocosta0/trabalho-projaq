package com.pucrs.planosdevoo.domain.ports.in;

import java.time.LocalDateTime;

public record CriarPlanoCommand(Long pilotoId, String prefixoAeronave, String codigoOrigem,
                                String codigoDestino, LocalDateTime partida) {
}
