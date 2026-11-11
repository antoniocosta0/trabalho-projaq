package com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PlanoDeVooEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.TrechoEmbeddable;
import java.util.List;

public final class PlanoDeVooMapper {

    private PlanoDeVooMapper() { }

    public static PlanoDeVoo paraDominio(PlanoDeVooEntity e) {
        Aeronave aeronave = FrotaMapper.paraDominio(e.getAeronave());
        List<Trecho> trechos = e.getTrechos().stream()
                .map(t -> new Trecho(t.getOrdem(), new Aerovia(t.getAeroviaId(), t.getOrigem(), t.getDestino(), t.getDistanciaKm()), t.getAltitude(),
                        t.getHorarioEntrada(), aeronave.getVelocidadeCruzeiroKmh(), pousoDe(t)))
                .toList();
        return PlanoDeVoo.reconstituir(
                e.getId(),
                FrotaMapper.paraDominio(e.getPiloto()),
                aeronave,
                e.getOrigem(),
                e.getDestino(),
                e.getPartida(),
                e.getSituacao(),
                trechos);
    }

    private static Pouso pousoDe(TrechoEmbeddable t) {
        return t.getNumeroVaga() == null ? null : new Pouso(t.getNumeroVaga(), t.getPermanenciaMinutos());
    }
}
