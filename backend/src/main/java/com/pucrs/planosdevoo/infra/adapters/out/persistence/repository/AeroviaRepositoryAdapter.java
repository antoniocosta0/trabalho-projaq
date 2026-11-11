package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroviaEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.MalhaAereaMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AeroviaRepositoryAdapter implements AeroviaRepository {

    private final AeroviaJpaRepository jpa;
    private final AeroportoJpaRepository aeroportos;

    public AeroviaRepositoryAdapter(AeroviaJpaRepository jpa, AeroportoJpaRepository aeroportos) {
        this.jpa = jpa;
        this.aeroportos = aeroportos;
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
        AeroviaEntity entidade = new AeroviaEntity(aerovia.getId(),
                aeroportos.getReferenceById(aerovia.getOrigem()),
                aeroportos.getReferenceById(aerovia.getDestino()), aerovia.getDistanciaKm());
        jpa.save(entidade);
        return aerovia;
    }
}
