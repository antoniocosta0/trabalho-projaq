package com.pucrs.cadastro.domain.ports.out;

import com.pucrs.cadastro.domain.model.Aeroporto;
import java.util.List;
import java.util.Optional;

public interface AeroportoRepository {

    List<Aeroporto> listarTodos();

    Optional<Aeroporto> buscarPorCodigo(String codigo);

    Aeroporto salvar(Aeroporto aeroporto);
}
