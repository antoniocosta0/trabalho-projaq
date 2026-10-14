package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;
import java.util.List;

public class ConsultarAeronavesUC {

    private final RepositorioAeronaves aeronaves;

    public ConsultarAeronavesUC(RepositorioAeronaves aeronaves) {
        this.aeronaves = aeronaves;
    }

    public List<Aeronave> executar() {
        return aeronaves.listarTodas();
    }
}
