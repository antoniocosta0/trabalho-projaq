package com.pucrs.cadastro.infra.adapters.out.persistence.repository;

import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.ports.out.AeroportoRepository;
import com.pucrs.cadastro.infra.adapters.out.persistence.mapper.MalhaAereaMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AeroportoRepositoryAdapter implements AeroportoRepository {

    private final AeroportoJpaRepository jpa;

    public AeroportoRepositoryAdapter(AeroportoJpaRepository jpa) {
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

    @Override
    public Aeroporto salvar(Aeroporto aeroporto) {
        return MalhaAereaMapper.paraDominio(jpa.save(MalhaAereaMapper.paraEntidade(aeroporto)));
    }
}
