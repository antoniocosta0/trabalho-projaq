package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPilotos;
import java.util.List;

public class ConsultarPilotosUC {

    private final RepositorioPilotos pilotos;

    public ConsultarPilotosUC(RepositorioPilotos pilotos) {
        this.pilotos = pilotos;
    }

    public List<Piloto> executar() {
        return pilotos.listarTodos();
    }
}
