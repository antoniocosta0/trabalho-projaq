package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import java.util.List;

public class ListarPlanosUC {

    private final RepositorioPlanosDeVoo planos;

    public ListarPlanosUC(RepositorioPlanosDeVoo planos) {
        this.planos = planos;
    }

    // Sem situação informada, lista todos os planos.
    public List<PlanoDeVoo> executar(SituacaoPlano situacao) {
        return situacao == null ? planos.listarTodos() : planos.listarPorSituacao(situacao);
    }
}
