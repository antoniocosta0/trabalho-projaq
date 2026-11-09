package com.pucrs.planosdevoo.domain.services;

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
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
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
    private final AeroportoRepository aeroportos;
    private final AeroviaRepository aerovias;
    private final ReservaRepository reservas;
    private final DisponibilidadeService disponibilidade;
    private final TransacaoPort transacao;
    private final Clock relogio;

    public PlanoDeVooService(PlanoDeVooRepository planos, PilotoRepository pilotos, AeronaveRepository aeronaves,
                             AeroportoRepository aeroportos, AeroviaRepository aerovias, ReservaRepository reservas,
                             DisponibilidadeService disponibilidade, TransacaoPort transacao, Clock relogio) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
        this.reservas = reservas;
        this.disponibilidade = disponibilidade;
        this.transacao = transacao;
        this.relogio = relogio;
    }

    @Override
    public PlanoDeVoo criar(PlanoCommand comando) {
        PlanoDeVoo plano = PlanoDeVoo.novo(buscarPiloto(comando.pilotoId()), buscarAeronave(comando.prefixoAeronave()),
                buscarAeroporto(comando.codigoOrigem()), buscarAeroporto(comando.codigoDestino()), comando.partida());
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo alterar(Long id, PlanoCommand comando) {
        PlanoDeVoo plano = buscar(id);
        plano.alterarDados(buscarPiloto(comando.pilotoId()), buscarAeronave(comando.prefixoAeronave()),
                buscarAeroporto(comando.codigoOrigem()), buscarAeroporto(comando.codigoDestino()), comando.partida());
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo buscar(Long id) {
        return planos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + id + " não encontrado"));
    }

    @Override
    public List<PlanoDeVoo> listar(SituacaoPlano situacao) {
        return situacao == null ? planos.listarTodos() : planos.listarPorSituacao(situacao);
    }

    @Override
    public PlanoDeVoo adicionarTrecho(Long planoId, TrechoCommand comando) {
        PlanoDeVoo plano = buscar(planoId);
        plano.adicionarTrecho(buscarAerovia(comando.aeroviaId()), comando.altitude(), comando.horarioEntrada(),
                pousoDe(comando));
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo alterarTrecho(Long planoId, int ordem, TrechoCommand comando) {
        PlanoDeVoo plano = buscar(planoId);
        plano.alterarTrecho(ordem, buscarAerovia(comando.aeroviaId()), comando.altitude(), comando.horarioEntrada(),
                pousoDe(comando));
        return planos.salvar(plano);
    }

    @Override
    public PlanoDeVoo removerTrecho(Long planoId, int ordem) {
        PlanoDeVoo plano = buscar(planoId);
        plano.removerTrecho(ordem);
        return planos.salvar(plano);
    }

    @Override
    public List<String> validar(Long planoId) {
        PlanoDeVoo plano = buscar(planoId);
        List<String> problemas = new ArrayList<>(plano.validar());
        problemas.addAll(disponibilidade.conflitos(plano));
        return problemas;
    }

    // §6.5: a disponibilidade é conferida de novo e a reserva é tudo ou nada
    @Override
    public PlanoDeVoo aprovar(Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = buscar(planoId);
            plano.aprovar(disponibilidade.conflitos(plano));
            PlanoDeVoo aprovado = planos.salvar(plano);
            reservas.reservar(aprovado.reservasDeAerovia(), aprovado.reservasDeVaga());
            return aprovado;
        });
    }

    @Override
    public PlanoDeVoo cancelar(Long planoId) {
        return transacao.executar(() -> {
            PlanoDeVoo plano = buscar(planoId);
            plano.cancelar(agora());
            reservas.liberarReservasDoPlano(planoId);
            return planos.salvar(plano);
        });
    }

    @Override
    public PlanoDeVoo abandonar(Long planoId) {
        PlanoDeVoo plano = buscar(planoId);
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

    private Aeroporto buscarAeroporto(String codigo) {
        return aeroportos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }

    private Aerovia buscarAerovia(String id) {
        return aerovias.buscarPorId(id)
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
