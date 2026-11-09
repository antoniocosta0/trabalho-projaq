package com.pucrs.planosdevoo.domain.services;

import com.pucrs.planosdevoo.domain.exceptions.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.AeronaveFactory;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.in.CadastrarAeronaveCommand;
import com.pucrs.planosdevoo.domain.ports.in.FrotaUseCase;
import com.pucrs.planosdevoo.domain.ports.out.AeronaveRepository;
import com.pucrs.planosdevoo.domain.ports.out.PilotoRepository;
import java.util.List;

public class FrotaService implements FrotaUseCase {

    private final PilotoRepository pilotos;
    private final AeronaveRepository aeronaves;

    public FrotaService(PilotoRepository pilotos, AeronaveRepository aeronaves) {
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
    }

    @Override
    public List<Piloto> listarPilotos() {
        return pilotos.listarTodos();
    }

    @Override
    public List<Aeronave> listarAeronaves() {
        return aeronaves.listarTodas();
    }

    @Override
    public Piloto cadastrarPiloto(String nome) {
        return pilotos.salvar(new Piloto(null, nome));
    }

    @Override
    public Aeronave cadastrarAeronave(CadastrarAeronaveCommand c) {
        Aeronave aeronave = AeronaveFactory.criar(c.categoria(), c.prefixo(), c.velocidadeCruzeiroKmh(),
                c.autonomiaKm(), c.empresaManutencao(), c.companhiaAerea(), c.capacidadePassageiros(),
                c.capacidadeToneladas());
        if (aeronaves.buscarPorPrefixo(c.prefixo()).isPresent()) {
            throw new OperacaoNaoPermitidaException("A aeronave " + c.prefixo() + " já está cadastrada");
        }
        return aeronaves.salvar(aeronave);
    }
}
