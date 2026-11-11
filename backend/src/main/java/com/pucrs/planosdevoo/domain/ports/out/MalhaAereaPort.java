package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import java.util.Optional;

public interface MalhaAereaPort {

    Optional<Aeroporto> buscarAeroporto(String codigo);

    Optional<Aerovia> buscarAerovia(String id);
}
