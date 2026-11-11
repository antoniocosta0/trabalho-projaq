package com.pucrs.planosdevoo.infra.adapters.in.rest;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

final class Localizacao {

    private Localizacao() { }

    // parte da URL da requisição, então respeita o prefixo repassado pelo gateway
    static URI doCriado(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(id).toUri();
    }
}
