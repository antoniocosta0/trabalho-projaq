package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;

public class ConsultarPlanoUC {

    private final RepositorioPlanosDeVoo planos;

    public ConsultarPlanoUC(RepositorioPlanosDeVoo planos) {
        this.planos = planos;
    }

    public PlanoDeVoo executar(Long id) {
        return planos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + id + " não encontrado"));
    }
}
