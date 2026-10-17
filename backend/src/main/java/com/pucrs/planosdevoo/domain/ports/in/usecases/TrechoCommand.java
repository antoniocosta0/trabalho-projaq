package com.pucrs.planosdevoo.domain.ports.in.usecases;

import java.time.LocalDateTime;

// Dados de um trecho como chegam na requisição. numeroVaga e permanenciaMinutos
// vêm juntos quando há pouso no fim do trecho; os dois nulos = sobrevoo.
public record TrechoCommand(String aeroviaId, int altitude, LocalDateTime horarioEntrada,
                            Integer numeroVaga, Integer permanenciaMinutos) {
}
