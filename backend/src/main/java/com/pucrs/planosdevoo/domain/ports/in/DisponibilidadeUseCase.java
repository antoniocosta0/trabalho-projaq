package com.pucrs.planosdevoo.domain.ports.in;

import java.time.LocalDateTime;
import java.util.List;

public interface DisponibilidadeUseCase {

    List<Integer> altitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim);

    List<Integer> vagasDisponiveis(String codigoAeroporto, LocalDateTime inicio, LocalDateTime fim);
}
