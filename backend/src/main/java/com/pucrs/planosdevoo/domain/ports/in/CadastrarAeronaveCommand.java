package com.pucrs.planosdevoo.domain.ports.in;

public record CadastrarAeronaveCommand(String prefixo, String categoria, double velocidadeCruzeiroKmh,
                                       double autonomiaKm, String empresaManutencao, String companhiaAerea,
                                       Integer capacidadePassageiros, Double capacidadeToneladas) {
}
