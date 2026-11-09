package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import java.util.List;

public class ConsultarAeroportosUC {

    private final AeroportoRepository aeroportos;

    public ConsultarAeroportosUC(AeroportoRepository aeroportos) {
        this.aeroportos = aeroportos;
    }

    public List<Aeroporto> executar() {
        return aeroportos.listarTodos();
    }
}
