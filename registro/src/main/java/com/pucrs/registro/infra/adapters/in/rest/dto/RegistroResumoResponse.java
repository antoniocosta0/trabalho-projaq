package com.pucrs.registro.infra.adapters.in.rest.dto;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import java.time.LocalDateTime;

public record RegistroResumoResponse(String eventoId, Long planoId, LocalDateTime aprovadoEm,
                                     LocalDateTime recebidoEm) {

    public static RegistroResumoResponse de(RegistroAprovacao r) {
        return new RegistroResumoResponse(r.getEventoId(), r.getPlanoId(), r.getAprovadoEm(), r.getRecebidoEm());
    }
}
