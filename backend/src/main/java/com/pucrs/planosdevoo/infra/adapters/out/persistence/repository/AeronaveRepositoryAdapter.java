package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.FrotaMapper;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.repository.AeronaveJpaRepository;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AeronaveRepositoryAdapter implements AeronaveRepository {

    private final AeronaveJpaRepository jpa;

    public AeronaveRepositoryAdapter(AeronaveJpaRepository jpa) {
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

    @Override
    public Aeronave salvar(Aeronave aeronave) {
        return FrotaMapper.paraDominio(jpa.save(FrotaMapper.paraEntidade(aeronave)));
    }
}
