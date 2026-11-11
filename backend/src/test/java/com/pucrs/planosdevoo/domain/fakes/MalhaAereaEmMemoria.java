package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.MalhaAereaPort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class MalhaAereaEmMemoria implements MalhaAereaPort {

    private static final Map<String, Integer> VAGAS = Map.of("POA", 41, "FLN", 16, "CWB", 26, "GRU", 115);

    private static final Map<String, Aerovia> AEROVIAS = Map.of(
            "R1", new Aerovia("R1", "POA", "FLN", 363),
            "R2", new Aerovia("R2", "POA", "GRU", 865),
            "R3", new Aerovia("R3", "GRU", "POA", 865),
            "R4", new Aerovia("R4", "FLN", "CWB", 245),
            "R5", new Aerovia("R5", "CWB", "FLN", 245),
            "R6", new Aerovia("R6", "CWB", "GRU", 360),
            "R7", new Aerovia("R7", "GRU", "CWB", 360),
            "R8", new Aerovia("R8", "FLN", "POA", 363));

    @Override
    public Optional<Aeroporto> buscarAeroporto(String codigo) {
        return Optional.ofNullable(VAGAS.get(codigo))
                .map(quantidade -> new Aeroporto(codigo, IntStream.rangeClosed(1, quantidade).boxed().toList()));
    }

    @Override
    public Optional<Aerovia> buscarAerovia(String id) {
        return Optional.ofNullable(AEROVIAS.get(id));
    }

    public static List<String> aerovias() {
        return AEROVIAS.keySet().stream().sorted().toList();
    }
}
