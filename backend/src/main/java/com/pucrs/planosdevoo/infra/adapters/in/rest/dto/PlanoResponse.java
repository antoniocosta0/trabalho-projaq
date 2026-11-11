package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import java.time.LocalDateTime;
import java.util.List;

public record PlanoResponse(Long id, String criador, Long pilotoId, String pilotoNome, String aeronavePrefixo,
                            String origem, String destino, LocalDateTime partida, String situacao,
                            List<TrechoResponse> trechos) {

    public static PlanoResponse de(PlanoDeVoo p) {
        return new PlanoResponse(p.getId(), p.getCriador(), p.getPiloto().getId(), p.getPiloto().getNome(),
                p.getAeronave().getPrefixo(), p.getOrigem(), p.getDestino(),
                p.getPartida(), p.getSituacao().name(),
                p.getTrechos().stream().map(TrechoResponse::de).toList());
    }
}
