package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import java.util.List;
import java.util.Optional;

public interface AeronaveRepository {
    List<Aeronave> listarTodas();
    Optional<Aeronave> buscarPorPrefixo(String prefixo);

    Aeronave salvar(Aeronave aeronave);
}
