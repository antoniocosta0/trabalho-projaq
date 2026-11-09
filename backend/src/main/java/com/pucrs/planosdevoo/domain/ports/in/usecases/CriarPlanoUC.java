package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;

public class CriarPlanoUC {

    private final PlanoDeVooRepository planos;
    private final PilotoRepository pilotos;
    private final AeronaveRepository aeronaves;
    private final AeroportoRepository aeroportos;

    public CriarPlanoUC(PlanoDeVooRepository planos, PilotoRepository pilotos,
                        AeronaveRepository aeronaves, AeroportoRepository aeroportos) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
    }

    public PlanoDeVoo executar(CriarPlanoCommand comando) {
        Piloto piloto = pilotos.buscarPorId(comando.pilotoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Piloto " + comando.pilotoId() + " não encontrado"));
        Aeronave aeronave = aeronaves.buscarPorPrefixo(comando.prefixoAeronave())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeronave " + comando.prefixoAeronave() + " não encontrada"));
        Aeroporto origem = buscarAeroporto(comando.codigoOrigem());
        Aeroporto destino = buscarAeroporto(comando.codigoDestino());

        return planos.salvar(PlanoDeVoo.novo(piloto, aeronave, origem, destino, comando.partida()));
    }

    private Aeroporto buscarAeroporto(String codigo) {
        return aeroportos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }
}
