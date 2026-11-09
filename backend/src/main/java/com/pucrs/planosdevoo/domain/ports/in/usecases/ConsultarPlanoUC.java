package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;

public class ConsultarPlanoUC {

    private final PlanoDeVooRepository planos;

    public ConsultarPlanoUC(PlanoDeVooRepository planos) {
        this.planos = planos;
    }

    public PlanoDeVoo executar(Long id) {
        return planos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + id + " não encontrado"));
    }
}
