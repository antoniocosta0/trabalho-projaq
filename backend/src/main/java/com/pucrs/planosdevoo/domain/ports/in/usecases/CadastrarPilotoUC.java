package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPilotos;

public class CadastrarPilotoUC {

    private final RepositorioPilotos pilotos;

    public CadastrarPilotoUC(RepositorioPilotos pilotos) {
        this.pilotos = pilotos;
    }

    // O id é gerado pelo banco.
    public Piloto executar(String nome) {
        return pilotos.salvar(new Piloto(null, nome));
    }
}
