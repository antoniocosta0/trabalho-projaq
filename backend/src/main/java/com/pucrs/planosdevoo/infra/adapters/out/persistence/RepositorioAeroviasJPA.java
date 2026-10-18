package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAeroviasJPA implements RepositorioAerovias {

    private final AeroviaSpringDataRepository jpa;
    private final AeroportoSpringDataRepository aeroportos;

    public RepositorioAeroviasJPA(AeroviaSpringDataRepository jpa, AeroportoSpringDataRepository aeroportos) {
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

    // Origem e destino já existem (o caso de uso conferiu): basta a referência.
    @Override
    public Aerovia salvar(Aerovia aerovia) {
        AeroviaJpaEntity entidade = new AeroviaJpaEntity(aerovia.getId(),
                aeroportos.getReferenceById(aerovia.getOrigem().getCodigo()),
                aeroportos.getReferenceById(aerovia.getDestino().getCodigo()), aerovia.getDistanciaKm());
        jpa.save(entidade);
        return aerovia;
    }
}
