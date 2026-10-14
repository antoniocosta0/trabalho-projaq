package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;

public final class PlanoDeVooMapper {

    private PlanoDeVooMapper() { }

    public static PlanoDeVoo paraDominio(PlanoDeVooJpaEntity e) {
        return PlanoDeVoo.reconstituir(
                e.getId(),
                FrotaMapper.paraDominio(e.getPiloto()),
                FrotaMapper.paraDominio(e.getAeronave()),
                MalhaAereaMapper.paraDominio(e.getOrigem()),
                MalhaAereaMapper.paraDominio(e.getDestino()),
                e.getPartida(),
                e.getSituacao());
    }
}
