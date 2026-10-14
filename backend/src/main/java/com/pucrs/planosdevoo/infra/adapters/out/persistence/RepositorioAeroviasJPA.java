package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAeroviasJPA implements RepositorioAerovias {

    private final AeroviaSpringDataRepository jpa;

    public RepositorioAeroviasJPA(AeroviaSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Aerovia> listarTodas() {
        return jpa.findAll().stream().map(MalhaAereaMapper::paraDominio).toList();
    }

    @Override
    public Optional<Aerovia> buscarPorId(String id) {
        return jpa.findById(id).map(MalhaAereaMapper::paraDominio);
    }
}
