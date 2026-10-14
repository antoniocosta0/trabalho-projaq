package com.pucrs.planosdevoo.domain.ports.in.usecases;

import java.time.LocalDateTime;

// A requisição traz só identificadores (piloto, prefixo, códigos dos aeroportos).
// O caso de uso é quem busca os objetos completos para montar o plano.
public record CriarPlanoCommand(Long pilotoId, String prefixoAeronave, String codigoOrigem,
                                String codigoDestino, LocalDateTime partida) {
}
