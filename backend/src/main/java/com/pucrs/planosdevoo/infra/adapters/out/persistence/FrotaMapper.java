package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import com.pucrs.planosdevoo.domain.model.FabricaDeAeronaves;
import com.pucrs.planosdevoo.domain.model.Piloto;

public final class FrotaMapper {

    private FrotaMapper() { }

    public static Piloto paraDominio(PilotoJpaEntity e) {
        return new Piloto(e.getId(), e.getNome());
    }

    public static PilotoJpaEntity paraEntidade(Piloto p) {
        return new PilotoJpaEntity(p.getId(), p.getNome());
    }

    // Quem escolhe a subclasse pela coluna "categoria" é a FabricaDeAeronaves,
    // a mesma do cadastro: o switch por categoria existe num lugar só.
    public static Aeronave paraDominio(AeronaveJpaEntity e) {
        return FabricaDeAeronaves.criar(new DadosAeronave(e.getPrefixo(), e.getCategoria(),
                e.getVelocidadeCruzeiroKmh(), e.getAutonomiaKm(), e.getEmpresaManutencao(), e.getCompanhiaAerea(),
                e.getCapacidadePassageiros(), e.getCapacidadeToneladas()));
    }

    public static AeronaveJpaEntity paraEntidade(DadosAeronave d) {
        return new AeronaveJpaEntity(d.prefixo(), d.categoria(), d.velocidadeCruzeiroKmh(), d.autonomiaKm(),
                d.empresaManutencao(), d.companhiaAerea(), d.capacidadePassageiros(), d.capacidadeToneladas());
    }
}
