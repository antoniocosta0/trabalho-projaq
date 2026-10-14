package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAeroportosJPA implements RepositorioAeroportos {

    private final AeroportoSpringDataRepository jpa;

    public RepositorioAeroportosJPA(AeroportoSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Aeroporto> listarTodos() {
        return jpa.findAll().stream().map(MalhaAereaMapper::paraDominio).toList();
    }

    @Override
    public Optional<Aeroporto> buscarPorCodigo(String codigo) {
        return jpa.findById(codigo).map(MalhaAereaMapper::paraDominio);
    }
}
