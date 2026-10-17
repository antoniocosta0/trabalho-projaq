package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;

// UC11 — Abandonar plano. Plano em edição não tem reservas, então não há nada
// a liberar.
public class AbandonarPlanoUC {

    private final RepositorioPlanosDeVoo planos;

    public AbandonarPlanoUC(RepositorioPlanosDeVoo planos) {
        this.planos = planos;
    }

    public PlanoDeVoo executar(Long planoId) {
        PlanoDeVoo plano = planos.buscarPorId(planoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + planoId + " não encontrado"));
        plano.abandonar();
        return planos.salvar(plano);
    }
}
