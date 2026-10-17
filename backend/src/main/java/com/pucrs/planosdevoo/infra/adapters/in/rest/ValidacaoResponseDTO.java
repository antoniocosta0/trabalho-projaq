package com.pucrs.planosdevoo.infra.adapters.in.rest;

import java.util.List;

public record ValidacaoResponseDTO(Long planoId, boolean valido, List<String> problemas) {

    public static ValidacaoResponseDTO de(Long planoId, List<String> problemas) {
        return new ValidacaoResponseDTO(planoId, problemas.isEmpty(), problemas);
    }
}
