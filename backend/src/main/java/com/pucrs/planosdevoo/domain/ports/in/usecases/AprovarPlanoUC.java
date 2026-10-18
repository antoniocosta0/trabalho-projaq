package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.VerificadorDeConflitos;
import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;

// UC10 — Aprovar plano. Integral: ou aprova e reserva tudo, ou não muda nada.
public class AprovarPlanoUC {

    private final RepositorioPlanosDeVoo planos;
    private final RepositorioReservas reservas;
    private final VerificadorDeConflitos verificador;
    private final Transacao transacao;

    public AprovarPlanoUC(RepositorioPlanosDeVoo planos, RepositorioReservas reservas,
                          VerificadorDeConflitos verificador, Transacao transacao) {
        this.planos = planos;
        this.reservas = reservas;
        this.verificador = verificador;
        this.transacao = transacao;
    }

    // Tudo roda numa transação só: a situação e as reservas são gravadas juntas.
    // A conferência é refeita aqui porque a disponibilidade consultada antes pode
    // ter mudado. Se duas aprovações passarem juntas pela conferência, o UNIQUE do
    // banco derruba a segunda no momento de reservar e a transação desfaz tudo.
    public PlanoDeVoo executar(Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = planos.buscarPorId(planoId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + planoId + " não encontrado"));
            plano.aprovar(verificador.verificar(plano));
            PlanoDeVoo aprovado = planos.salvar(plano);
            reservas.reservar(aprovado.reservasDeAerovia(), aprovado.reservasDeVaga());
            return aprovado;
        });
    }
}
