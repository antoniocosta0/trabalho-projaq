package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.Trecho;
import java.util.List;

public final class PlanoDeVooMapper {

    private PlanoDeVooMapper() { }

    public static PlanoDeVoo paraDominio(PlanoDeVooJpaEntity e) {
        Aeronave aeronave = FrotaMapper.paraDominio(e.getAeronave());
        // A velocidade não fica salva no trecho: vem da aeronave do plano, que
        // é de onde ela sempre deve vir.
        List<Trecho> trechos = e.getTrechos().stream()
                .map(t -> new Trecho(t.getOrdem(), MalhaAereaMapper.paraDominio(t.getAerovia()), t.getAltitude(),
                        t.getHorarioEntrada(), aeronave.getVelocidadeCruzeiroKmh(), pousoDe(t)))
                .toList();
        return PlanoDeVoo.reconstituir(
                e.getId(),
                FrotaMapper.paraDominio(e.getPiloto()),
                aeronave,
                MalhaAereaMapper.paraDominio(e.getOrigem()),
                MalhaAereaMapper.paraDominio(e.getDestino()),
                e.getPartida(),
                e.getSituacao(),
                trechos);
    }

    private static Pouso pousoDe(TrechoJpaEmbeddable t) {
        return t.getNumeroVaga() == null ? null : new Pouso(t.getNumeroVaga(), t.getPermanenciaMinutos());
    }
}
