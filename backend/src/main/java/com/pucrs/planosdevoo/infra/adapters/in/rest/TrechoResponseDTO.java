package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Trecho;
import java.time.LocalDateTime;

public record TrechoResponseDTO(int ordem, String aerovia, String origem, String destino, int altitude,
                                LocalDateTime horarioEntrada, LocalDateTime horarioChegada,
                                String noDestino, Integer numeroVaga, Integer permanenciaMinutos,
                                LocalDateTime saidaDaVaga) {

    public static TrechoResponseDTO de(Trecho t) {
        boolean pousa = t.getPouso() != null;
        return new TrechoResponseDTO(t.getOrdem(), t.getAerovia().getId(),
                t.getAerovia().getOrigem().getCodigo(), t.getAerovia().getDestino().getCodigo(),
                t.getAltitude(), t.getHorarioEntrada(), t.horarioChegada(),
                pousa ? "POUSO" : "SOBREVOO",
                pousa ? t.getPouso().getNumeroVaga() : null,
                pousa ? t.getPouso().getPermanenciaMinutos() : null,
                pousa ? t.getPouso().saida(t.horarioChegada()) : null);
    }
}
