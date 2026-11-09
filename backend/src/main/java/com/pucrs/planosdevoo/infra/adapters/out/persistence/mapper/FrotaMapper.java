package com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronaveCarga;
import com.pucrs.planosdevoo.domain.model.AeronaveFactory;
import com.pucrs.planosdevoo.domain.model.AeronaveParticular;
import com.pucrs.planosdevoo.domain.model.AeronavePassageiros;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeronaveEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PilotoEntity;

public final class FrotaMapper {

    private FrotaMapper() { }

    public static Piloto paraDominio(PilotoEntity e) {
        return new Piloto(e.getId(), e.getNome());
    }

    public static PilotoEntity paraEntidade(Piloto p) {
        return new PilotoEntity(p.getId(), p.getNome());
    }

    public static Aeronave paraDominio(AeronaveEntity e) {
        return AeronaveFactory.criar(e.getCategoria(), e.getPrefixo(), e.getVelocidadeCruzeiroKmh(), e.getAutonomiaKm(),
                e.getEmpresaManutencao(), e.getCompanhiaAerea(), e.getCapacidadePassageiros(),
                e.getCapacidadeToneladas());
    }

    public static AeronaveEntity paraEntidade(Aeronave a) {
        return switch (a) {
            case AeronaveParticular p -> entidade(p, p.getEmpresaManutencao(), null, null, null);
            case AeronavePassageiros p -> entidade(p, null, p.getCompanhiaAerea(), p.getCapacidadePassageiros(), null);
            case AeronaveCarga c -> entidade(c, null, c.getCompanhiaAerea(), null, c.getCapacidadeToneladas());
        };
    }

    private static AeronaveEntity entidade(Aeronave a, String empresaManutencao, String companhiaAerea,
                                           Integer capacidadePassageiros, Double capacidadeToneladas) {
        return new AeronaveEntity(a.getPrefixo(), a.getCategoria(), a.getVelocidadeCruzeiroKmh(), a.getAutonomiaKm(),
                empresaManutencao, companhiaAerea, capacidadePassageiros, capacidadeToneladas);
    }
}
