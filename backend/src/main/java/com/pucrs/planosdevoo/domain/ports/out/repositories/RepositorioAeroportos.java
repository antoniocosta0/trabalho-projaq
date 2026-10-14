package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import java.util.List;
import java.util.Optional;

public interface RepositorioAeroportos {
    List<Aeroporto> listarTodos();
    Optional<Aeroporto> buscarPorCodigo(String codigo);
}
