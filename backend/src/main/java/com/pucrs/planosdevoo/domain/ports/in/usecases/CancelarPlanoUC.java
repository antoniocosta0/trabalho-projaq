package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.Clock;
import java.time.LocalDateTime;

// UC12 — Cancelar plano. Só plano aprovado com partida futura; libera todas as reservas.
public class CancelarPlanoUC {

    private final RepositorioPlanosDeVoo planos;
    private final RepositorioReservas reservas;
    private final Transacao transacao;
    private final Clock relogio;

    public CancelarPlanoUC(RepositorioPlanosDeVoo planos, RepositorioReservas reservas, Transacao transacao,
                           Clock relogio) {
        this.planos = planos;
        this.reservas = reservas;
        this.transacao = transacao;
        this.relogio = relogio;
    }

    public PlanoDeVoo executar(Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = planos.buscarPorId(planoId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + planoId + " não encontrado"));
            plano.cancelar(LocalDateTime.now(relogio));
            PlanoDeVoo cancelado = planos.salvar(plano);
            reservas.liberarReservasDoPlano(planoId);
            return cancelado;
        });
    }
}
