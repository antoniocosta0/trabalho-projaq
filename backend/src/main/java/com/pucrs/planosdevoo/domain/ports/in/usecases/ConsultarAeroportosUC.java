package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import java.util.List;

public class ConsultarAeroportosUC {

    private final RepositorioAeroportos aeroportos;

    public ConsultarAeroportosUC(RepositorioAeroportos aeroportos) {
        this.aeroportos = aeroportos;
    }

    public List<Aeroporto> executar() {
        return aeroportos.listarTodos();
    }
}
