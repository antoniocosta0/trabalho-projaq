package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import java.time.LocalDateTime;

public record PlanoResponseDTO(Long id, Long pilotoId, String pilotoNome, String aeronavePrefixo,
                            String origem, String destino, LocalDateTime partida, String situacao) {

    public static PlanoResponseDTO de(PlanoDeVoo p) {
        return new PlanoResponseDTO(p.getId(), p.getPiloto().getId(), p.getPiloto().getNome(),
                p.getAeronave().getPrefixo(), p.getOrigem().getCodigo(), p.getDestino().getCodigo(),
                p.getPartida(), p.getSituacao().name());
    }
}
