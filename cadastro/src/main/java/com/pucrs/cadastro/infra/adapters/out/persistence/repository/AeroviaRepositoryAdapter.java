package com.pucrs.cadastro.infra.adapters.out.persistence.repository;

import com.pucrs.cadastro.domain.model.Aerovia;
import com.pucrs.cadastro.domain.ports.out.AeroviaRepository;
import com.pucrs.cadastro.infra.adapters.out.persistence.mapper.MalhaAereaMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AeroviaRepositoryAdapter implements AeroviaRepository {

    private final AeroviaJpaRepository jpa;

    public AeroviaRepositoryAdapter(AeroviaJpaRepository jpa) {
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

    @Override
    public Aerovia salvar(Aerovia aerovia) {
        return MalhaAereaMapper.paraDominio(jpa.save(MalhaAereaMapper.paraEntidade(aerovia)));
    }
}
