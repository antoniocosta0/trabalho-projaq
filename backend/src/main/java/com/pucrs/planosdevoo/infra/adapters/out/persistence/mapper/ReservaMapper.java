package com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper;

import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.ReservaAeroviaEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.ReservaVagaEntity;

public final class ReservaMapper {

    private ReservaMapper() { }

    public static ReservaAerovia paraDominio(ReservaAeroviaEntity e) {
        return new ReservaAerovia(e.getPlanoId(), MalhaAereaMapper.paraDominio(e.getAerovia()), e.getAltitude(),
                new Slot(e.getSlotInicio()));
    }

    public static ReservaVaga paraDominio(ReservaVagaEntity e) {
        return new ReservaVaga(e.getPlanoId(), MalhaAereaMapper.paraDominio(e.getAeroporto()), e.getNumeroVaga(),
                new Slot(e.getSlotInicio()));
    }
}
