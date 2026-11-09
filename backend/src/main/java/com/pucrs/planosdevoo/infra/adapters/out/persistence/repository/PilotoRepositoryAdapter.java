package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.FrotaMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PilotoRepositoryAdapter implements PilotoRepository {

    private final PilotoJpaRepository jpa;

    public PilotoRepositoryAdapter(PilotoJpaRepository jpa) {
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
