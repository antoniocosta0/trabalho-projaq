package com.pucrs.planosdevoo.domain.services;

import com.pucrs.planosdevoo.domain.exceptions.AcessoNegadoException;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.PlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.PlanoDeVooUseCase;
import com.pucrs.planosdevoo.domain.ports.in.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import com.pucrs.planosdevoo.domain.ports.out.AprovacaoPublisherPort;
import com.pucrs.planosdevoo.domain.ports.out.MalhaAereaPort;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import com.pucrs.planosdevoo.domain.ports.out.TransacaoPort;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlanoDeVooService implements PlanoDeVooUseCase {

    private final PlanoDeVooRepository planos;
    private final PilotoRepository pilotos;
    private final AeronaveRepository aeronaves;
    private final MalhaAereaPort malhaAerea;
    private final ReservaRepository reservas;
    private final DisponibilidadeService disponibilidade;
    private final AprovacaoPublisherPort aprovacoes;
    private final TransacaoPort transacao;
    private final Clock relogio;

    public PlanoDeVooService(PlanoDeVooRepository planos, PilotoRepository pilotos, AeronaveRepository aeronaves,
                             MalhaAereaPort malhaAerea, ReservaRepository reservas,
                             DisponibilidadeService disponibilidade, AprovacaoPublisherPort aprovacoes,
                             TransacaoPort transacao, Clock relogio) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.malhaAerea = malhaAerea;
        this.reservas = reservas;
        this.disponibilidade = disponibilidade;
        this.aprovacoes = aprovacoes;
        this.transacao = transacao;
        this.relogio = relogio;
    }

    @Override
    public PlanoDeVoo criar(String usuario, PlanoCommand comando) {
        PlanoDeVoo plano = PlanoDeVoo.novo(usuario, buscarPiloto(comando.pilotoId()),
                buscarAeronave(comando.prefixoAeronave()), aeroportoExistente(comando.codigoOrigem()),
                aeroportoExistente(comando.codigoDestino()), comando.partida());
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo alterar(String usuario, Long id, PlanoCommand comando) {
        PlanoDeVoo plano = buscar(usuario, id);
        plano.alterarDados(buscarPiloto(comando.pilotoId()), buscarAeronave(comando.prefixoAeronave()),
                aeroportoExistente(comando.codigoOrigem()), aeroportoExistente(comando.codigoDestino()),
                comando.partida());
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo buscar(String usuario, Long id) {
        PlanoDeVoo plano = planos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + id + " não encontrado"));
        if (!plano.pertenceA(usuario)) {
            throw new AcessoNegadoException("O plano " + id + " pertence a outro usuário");
        }
        return plano;
    }

    @Override
    public List<PlanoDeVoo> listar(String usuario, SituacaoPlano situacao) {
        if (situacao == null) {
            return planos.listarPorCriador(usuario);
        }
        return planos.listarPorCriadorESituacao(usuario, situacao);
    }

    @Override
    public PlanoDeVoo adicionarTrecho(String usuario, Long planoId, TrechoCommand comando) {
        PlanoDeVoo plano = buscar(usuario, planoId);
        plano.adicionarTrecho(buscarAerovia(comando.aeroviaId()), comando.altitude(), comando.horarioEntrada(),
                pousoDe(comando));
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo alterarTrecho(String usuario, Long planoId, int ordem, TrechoCommand comando) {
        PlanoDeVoo plano = buscar(usuario, planoId);
        plano.alterarTrecho(ordem, buscarAerovia(comando.aeroviaId()), comando.altitude(), comando.horarioEntrada(),
                pousoDe(comando));
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo removerTrecho(String usuario, Long planoId, int ordem) {
        PlanoDeVoo plano = buscar(usuario, planoId);
        plano.removerTrecho(ordem);
        return planos.salvar(plano);
    }

    @Override
    public List<String> validar(String usuario, Long planoId) {
        PlanoDeVoo plano = buscar(usuario, planoId);
        List<String> problemas = new ArrayList<>(plano.validar());
        problemas.addAll(disponibilidade.verificarRecursos(plano));
        return problemas;
    }

    // §6.5: a disponibilidade é conferida de novo e a reserva é tudo ou nada
    @Override
    public PlanoDeVoo aprovar(String usuario, Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = buscar(usuario, planoId);
            plano.aprovar(disponibilidade.verificarRecursos(plano));
            PlanoDeVoo aprovado = planos.salvar(plano);
            reservas.reservar(aprovado.reservasDeAerovia(), aprovado.reservasDeVaga());
            aprovacoes.publicar(aprovado, agora());
            return aprovado;
        });
    }

    @Override
    public PlanoDeVoo cancelar(String usuario, Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = buscar(usuario, planoId);
            plano.cancelar(agora());
            reservas.liberarReservasDoPlano(planoId);
            return planos.salvar(plano);
        });
    }

    @Override
    public PlanoDeVoo abandonar(String usuario, Long planoId) {
        PlanoDeVoo plano = buscar(usuario, planoId);
        plano.abandonar();
        return planos.salvar(plano);
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(relogio);
    }

    private Piloto buscarPiloto(Long id) {
        return pilotos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Piloto " + id + " não encontrado"));
    }

    private Aeronave buscarAeronave(String prefixo) {
        return aeronaves.buscarPorPrefixo(prefixo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeronave " + prefixo + " não encontrada"));
    }

    private String aeroportoExistente(String codigo) {
        return malhaAerea.buscarAeroporto(codigo)
                .map(Aeroporto::getCodigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }

    private Aerovia buscarAerovia(String id) {
        return malhaAerea.buscarAerovia(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + id + " não encontrada"));
    }

    private static Pouso pousoDe(TrechoCommand comando) {
        if (comando.numeroVaga() == null && comando.permanenciaMinutos() == null) {
            return null;
        }
        if (comando.numeroVaga() == null || comando.permanenciaMinutos() == null) {
            throw new IllegalArgumentException("Para pousar, informe a vaga e o tempo de permanência");
        }
        return new Pouso(comando.numeroVaga(), comando.permanenciaMinutos());
    }
}
