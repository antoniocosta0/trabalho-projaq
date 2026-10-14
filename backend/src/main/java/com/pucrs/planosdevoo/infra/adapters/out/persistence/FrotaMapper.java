package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronaveCarga;
import com.pucrs.planosdevoo.domain.model.AeronaveParticular;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Piloto;

public final class FrotaMapper {

    private FrotaMapper() { }

    public static Piloto paraDominio(PilotoJpaEntity e) {
        return new Piloto(e.getId(), e.getNome());
    }

    // A coluna "categoria" decide qual subclasse do domínio é criada.
    // Categoria nova = um case novo aqui + a subclasse nova no domínio.
    public static Aeronave paraDominio(AeronaveJpaEntity e) {
        return switch (e.getCategoria()) {
            case "PARTICULAR" -> new AeronaveParticular(e.getPrefixo(), e.getVelocidadeCruzeiroKmh(),
                    e.getAutonomiaKm(), e.getEmpresaManutencao());
            case "PASSAGEIROS" -> new AeronavePassageiros(e.getPrefixo(), e.getVelocidadeCruzeiroKmh(),
                    e.getAutonomiaKm(), e.getCompanhiaAerea(), e.getCapacidadePassageiros());
            case "CARGA" -> new AeronaveCarga(e.getPrefixo(), e.getVelocidadeCruzeiroKmh(),
                    e.getAutonomiaKm(), e.getCompanhiaAerea(), e.getCapacidadeToneladas());
            default -> throw new IllegalStateException("Categoria de aeronave desconhecida: " + e.getCategoria());
        };
    }
}
