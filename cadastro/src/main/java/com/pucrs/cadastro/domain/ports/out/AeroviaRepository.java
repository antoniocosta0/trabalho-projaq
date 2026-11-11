package com.pucrs.cadastro.domain.ports.out;

import com.pucrs.cadastro.domain.model.Aerovia;
import java.util.List;
import java.util.Optional;

public interface AeroviaRepository {

    List<Aerovia> listarTodas();

    Optional<Aerovia> buscarPorId(String id);

    Aerovia salvar(Aerovia aerovia);
}
