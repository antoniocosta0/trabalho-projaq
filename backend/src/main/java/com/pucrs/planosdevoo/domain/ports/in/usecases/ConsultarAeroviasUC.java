package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import java.util.List;

public class ConsultarAeroviasUC {

    private final RepositorioAerovias aerovias;

    public ConsultarAeroviasUC(RepositorioAerovias aerovias) {
        this.aerovias = aerovias;
    }

    public List<Aerovia> executar() {
        return aerovias.listarTodas();
    }
}
