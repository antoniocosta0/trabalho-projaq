package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import java.util.ArrayList;
import java.util.List;

// UC09 — Validar plano. Não muda a situação e não cria reserva.
public class ValidarPlanoUC {

    private final RepositorioPlanosDeVoo planos;

    public ValidarPlanoUC(RepositorioPlanosDeVoo planos) {
        this.planos = planos;
    }

    public List<String> executar(Long planoId) {
        PlanoDeVoo plano = planos.buscarPorId(planoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + planoId + " não encontrado"));
        List<String> problemas = new ArrayList<>(plano.validar());

        // PENDENTE (frente C): verificar a disponibilidade das altitudes e das
        // vagas nos slots de cada trecho, consultando as reservas dos outros
        // planos, e acrescentar os conflitos em "problemas".

        return problemas;
    }
}
