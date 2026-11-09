package com.pucrs.planosdevoo.domain.model;

public final class AeronaveFactory {

    private AeronaveFactory() { }

    public static Aeronave criar(String categoria, String prefixo, double velocidadeCruzeiroKmh, double autonomiaKm,
                                 String empresaManutencao, String companhiaAerea, Integer capacidadePassageiros,
                                 Double capacidadeToneladas) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria da aeronave é obrigatória");
        }
        return switch (categoria) {
            case "PARTICULAR" -> new AeronaveParticular(prefixo, velocidadeCruzeiroKmh, autonomiaKm, empresaManutencao);
            case "PASSAGEIROS" -> new AeronavePassageiros(prefixo, velocidadeCruzeiroKmh, autonomiaKm, companhiaAerea,
                    obrigatorio(capacidadePassageiros, "capacidadePassageiros"));
            case "CARGA" -> new AeronaveCarga(prefixo, velocidadeCruzeiroKmh, autonomiaKm, companhiaAerea,
                    obrigatorio(capacidadeToneladas, "capacidadeToneladas"));
            default -> throw new IllegalArgumentException("Categoria de aeronave desconhecida: " + categoria
                    + " (use PARTICULAR, PASSAGEIROS ou CARGA)");
        };
    }

    private static <T> T obrigatorio(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " é obrigatório para esta categoria");
        }
        return valor;
    }
}
