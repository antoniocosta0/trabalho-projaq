package com.pucrs.cadastro.infra.adapters.out.persistence.mapper;

import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.model.Aerovia;
import com.pucrs.cadastro.infra.adapters.out.persistence.entity.AeroportoEntity;
import com.pucrs.cadastro.infra.adapters.out.persistence.entity.AeroviaEntity;

public final class MalhaAereaMapper {

    private MalhaAereaMapper() { }

    public static Aeroporto paraDominio(AeroportoEntity e) {
        return new Aeroporto(e.getCodigo(), e.getNome(), e.getVagas());
    }

    public static AeroportoEntity paraEntidade(Aeroporto a) {
        return new AeroportoEntity(a.getCodigo(), a.getNome(), a.getVagas());
    }

    public static Aerovia paraDominio(AeroviaEntity e) {
        return new Aerovia(e.getId(), e.getOrigem(), e.getDestino(), e.getDistanciaKm());
    }

    public static AeroviaEntity paraEntidade(Aerovia a) {
        return new AeroviaEntity(a.getId(), a.getOrigem(), a.getDestino(), a.getDistanciaKm());
    }
}
