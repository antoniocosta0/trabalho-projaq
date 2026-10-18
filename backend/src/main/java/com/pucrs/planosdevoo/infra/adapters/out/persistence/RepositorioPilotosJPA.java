package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPilotos;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPilotosJPA implements RepositorioPilotos {

    private final PilotoSpringDataRepository jpa;

    public RepositorioPilotosJPA(PilotoSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Piloto> listarTodos() {
        return jpa.findAll().stream().map(FrotaMapper::paraDominio).toList();
    }

    @Override
    public Optional<Piloto> buscarPorId(Long id) {
        return jpa.findById(id).map(FrotaMapper::paraDominio);
    }

    @Override
    public Piloto salvar(Piloto piloto) {
        return FrotaMapper.paraDominio(jpa.save(FrotaMapper.paraEntidade(piloto)));
    }
}
