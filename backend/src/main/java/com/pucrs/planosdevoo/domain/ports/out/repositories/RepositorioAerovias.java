package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import java.util.List;
import java.util.Optional;

public interface RepositorioAerovias {
    List<Aerovia> listarTodas();
    Optional<Aerovia> buscarPorId(String id);
    Aerovia salvar(Aerovia aerovia);
}
