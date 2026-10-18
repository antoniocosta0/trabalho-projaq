package com.pucrs.planosdevoo.domain.model;

// Único ponto que conhece as subclasses de Aeronave: a categoria escolhe qual
// é criada. Usada no cadastro (POST /aeronaves) e ao ler do banco.
// Categoria nova = um case novo aqui + a subclasse nova; a validação do plano
// não muda, porque cada subclasse responde podeOperar() sozinha.
public final class FabricaDeAeronaves {

    private FabricaDeAeronaves() { }

    public static Aeronave criar(DadosAeronave d) {
        if (d.categoria() == null) {
            throw new IllegalArgumentException("Categoria da aeronave é obrigatória");
        }
        return switch (d.categoria()) {
            case "PARTICULAR" -> new AeronaveParticular(d.prefixo(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                    d.empresaManutencao());
            case "PASSAGEIROS" -> new AeronavePassageiros(d.prefixo(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                    d.companhiaAerea(), obrigatorio(d.capacidadePassageiros(), "capacidadePassageiros", d));
            case "CARGA" -> new AeronaveCarga(d.prefixo(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                    d.companhiaAerea(), obrigatorio(d.capacidadeToneladas(), "capacidadeToneladas", d));
            default -> throw new IllegalArgumentException("Categoria de aeronave desconhecida: " + d.categoria()
                    + " (use PARTICULAR, PASSAGEIROS ou CARGA)");
        };
    }

    private static <T> T obrigatorio(T valor, String campo, DadosAeronave d) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " é obrigatório para aeronave " + d.categoria());
        }
        return valor;
    }
}
