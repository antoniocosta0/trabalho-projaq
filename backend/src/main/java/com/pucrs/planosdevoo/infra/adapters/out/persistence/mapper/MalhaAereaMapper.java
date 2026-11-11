package com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroportoEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroviaEntity;

public final class MalhaAereaMapper {

    private MalhaAereaMapper() { }

    public static Aeroporto paraDominio(AeroportoEntity e) {
        return new Aeroporto(e.getCodigo(), e.getNome(), e.getQuantidadeVagas());
    }

    public static AeroportoEntity paraEntidade(Aeroporto a) {
        return new AeroportoEntity(a.getCodigo(), a.getNome(), a.getQuantidadeVagas());
    }

    public static Aerovia paraDominio(AeroviaEntity e) {
        return new Aerovia(e.getId(), e.getOrigem().getCodigo(), e.getDestino().getCodigo(), e.getDistanciaKm());
    }
}
