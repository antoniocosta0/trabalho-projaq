package com.pucrs.planosdevoo.domain.model;

// Os dados de cadastro de uma aeronave, de qualquer categoria. Os campos que
// não se aplicam à categoria ficam nulos (como na tabela única do banco).
public record DadosAeronave(String prefixo, String categoria, double velocidadeCruzeiroKmh, double autonomiaKm,
                            String empresaManutencao, String companhiaAerea, Integer capacidadePassageiros,
                            Double capacidadeToneladas) {
}
