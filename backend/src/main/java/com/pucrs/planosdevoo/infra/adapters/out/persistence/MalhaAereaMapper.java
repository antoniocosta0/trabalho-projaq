package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;

public final class MalhaAereaMapper {

    private MalhaAereaMapper() { }

    public static Aeroporto paraDominio(AeroportoJpaEntity e) {
        return new Aeroporto(e.getCodigo(), e.getNome(), e.getQuantidadeVagas());
    }

    public static Aerovia paraDominio(AeroviaJpaEntity e) {
        return new Aerovia(e.getId(), paraDominio(e.getOrigem()), paraDominio(e.getDestino()), e.getDistanciaKm());
    }
}
