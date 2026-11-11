package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.MalhaAereaPort;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.MalhaAereaMapper;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MalhaAereaLocalAdapter implements MalhaAereaPort {

    private final AeroportoJpaRepository aeroportos;
    private final AeroviaJpaRepository aerovias;

    public MalhaAereaLocalAdapter(AeroportoJpaRepository aeroportos, AeroviaJpaRepository aerovias) {
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    @Override
    public Optional<Aeroporto> buscarAeroporto(String codigo) {
        return aeroportos.findById(codigo).map(MalhaAereaMapper::paraDominio);
    }

    @Override
    public Optional<Aerovia> buscarAerovia(String id) {
        return aerovias.findById(id).map(MalhaAereaMapper::paraDominio);
    }
}
