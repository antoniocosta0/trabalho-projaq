package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import java.util.List;
import java.util.Optional;

public interface RepositorioAeronaves {
    List<Aeronave> listarTodas();
    Optional<Aeronave> buscarPorPrefixo(String prefixo);
}
