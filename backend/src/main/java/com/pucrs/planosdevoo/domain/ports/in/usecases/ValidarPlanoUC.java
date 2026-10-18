package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.VerificadorDeConflitos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import java.util.ArrayList;
import java.util.List;

// UC09 — Validar plano. Não muda a situação e não cria reserva.
public class ValidarPlanoUC {

    private final RepositorioPlanosDeVoo planos;
    private final VerificadorDeConflitos verificador;

    public ValidarPlanoUC(RepositorioPlanosDeVoo planos, VerificadorDeConflitos verificador) {
        this.planos = planos;
        this.verificador = verificador;
    }

    public List<String> executar(Long planoId) {
        PlanoDeVoo plano = planos.buscarPorId(planoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + planoId + " não encontrado"));
        List<String> problemas = new ArrayList<>(plano.validar());
        problemas.addAll(verificador.verificar(plano));
        return problemas;
    }
}
