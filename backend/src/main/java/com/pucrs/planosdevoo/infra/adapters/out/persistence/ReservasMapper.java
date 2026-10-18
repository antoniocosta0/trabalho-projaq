package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;

public final class ReservasMapper {

    private ReservasMapper() { }

    public static ReservaAerovia paraDominio(ReservaAeroviaJpaEntity e) {
        return new ReservaAerovia(e.getPlanoId(), MalhaAereaMapper.paraDominio(e.getAerovia()), e.getAltitude(),
                new Slot(e.getSlotInicio()));
    }

    public static ReservaVaga paraDominio(ReservaVagaJpaEntity e) {
        return new ReservaVaga(e.getPlanoId(), MalhaAereaMapper.paraDominio(e.getAeroporto()), e.getNumeroVaga(),
                new Slot(e.getSlotInicio()));
    }
}
