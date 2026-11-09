package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import java.util.List;

public class ConsultarPilotosUC {

    private final PilotoRepository pilotos;

    public ConsultarPilotosUC(PilotoRepository pilotos) {
        this.pilotos = pilotos;
    }

    public List<Piloto> executar() {
        return pilotos.listarTodos();
    }
}
