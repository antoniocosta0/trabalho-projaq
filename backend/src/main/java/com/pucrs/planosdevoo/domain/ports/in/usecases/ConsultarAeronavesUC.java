package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import java.util.List;

public class ConsultarAeronavesUC {

    private final AeronaveRepository aeronaves;

    public ConsultarAeronavesUC(AeronaveRepository aeronaves) {
        this.aeronaves = aeronaves;
    }

    public List<Aeronave> executar() {
        return aeronaves.listarTodas();
    }
}
