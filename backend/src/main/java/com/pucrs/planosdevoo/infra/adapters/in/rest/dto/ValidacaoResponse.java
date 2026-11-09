package com.pucrs.planosdevoo.infra.adapters.in.rest.dto;

import java.util.List;

public record ValidacaoResponse(Long planoId, boolean valido, List<String> problemas) {

    public static ValidacaoResponse de(Long planoId, List<String> problemas) {
        return new ValidacaoResponse(planoId, problemas.isEmpty(), problemas);
    }
}
