package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.Piloto;
import java.util.List;
import java.util.Optional;

public interface PilotoRepository {
    List<Piloto> listarTodos();
    Optional<Piloto> buscarPorId(Long id);
    Piloto salvar(Piloto piloto);
}
