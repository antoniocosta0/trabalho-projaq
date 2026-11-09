package com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PilotoEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeronaveEntity;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import com.pucrs.planosdevoo.domain.model.FabricaDeAeronaves;
import com.pucrs.planosdevoo.domain.model.Piloto;

public final class FrotaMapper {

    private FrotaMapper() { }

    public static Piloto paraDominio(PilotoEntity e) {
        return new Piloto(e.getId(), e.getNome());
    }

    public static PilotoEntity paraEntidade(Piloto p) {
        return new PilotoEntity(p.getId(), p.getNome());
    }

    // Quem escolhe a subclasse pela coluna "categoria" é a FabricaDeAeronaves,
    // a mesma do cadastro: o switch por categoria existe num lugar só.
    public static Aeronave paraDominio(AeronaveEntity e) {
        return FabricaDeAeronaves.criar(new DadosAeronave(e.getPrefixo(), e.getCategoria(),
                e.getVelocidadeCruzeiroKmh(), e.getAutonomiaKm(), e.getEmpresaManutencao(), e.getCompanhiaAerea(),
                e.getCapacidadePassageiros(), e.getCapacidadeToneladas()));
    }

    public static AeronaveEntity paraEntidade(DadosAeronave d) {
        return new AeronaveEntity(d.prefixo(), d.categoria(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                d.empresaManutencao(), d.companhiaAerea(), d.capacidadePassageiros(), d.capacidadeToneladas());
    }
}
