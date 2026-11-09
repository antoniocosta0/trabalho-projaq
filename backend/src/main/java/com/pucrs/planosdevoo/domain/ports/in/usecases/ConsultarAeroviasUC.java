package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import java.util.List;

public class ConsultarAeroviasUC {

    private final AeroviaRepository aerovias;

    public ConsultarAeroviasUC(AeroviaRepository aerovias) {
        this.aerovias = aerovias;
    }

    public List<Aerovia> executar() {
        return aerovias.listarTodas();
    }
}
