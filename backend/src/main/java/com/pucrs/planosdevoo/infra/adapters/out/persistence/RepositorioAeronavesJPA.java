package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAeronavesJPA implements RepositorioAeronaves {

    private final AeronaveSpringDataRepository jpa;

    public RepositorioAeronavesJPA(AeronaveSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Aeronave> listarTodas() {
        return jpa.findAll().stream().map(FrotaMapper::paraDominio).toList();
    }

    @Override
    public Optional<Aeronave> buscarPorPrefixo(String prefixo) {
        return jpa.findById(prefixo).map(FrotaMapper::paraDominio);
    }
}
