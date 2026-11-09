package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import java.util.List;
import java.util.Optional;

public interface AeroportoRepository {
    List<Aeroporto> listarTodos();
    Optional<Aeroporto> buscarPorCodigo(String codigo);
    Aeroporto salvar(Aeroporto aeroporto);
}
