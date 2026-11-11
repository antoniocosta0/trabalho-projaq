package com.pucrs.planosdevoo.domain.services;

import com.pucrs.planosdevoo.domain.exceptions.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.in.MalhaAereaUseCase;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import java.util.List;

public class MalhaAereaService implements MalhaAereaUseCase {

    private final AeroportoRepository aeroportos;
    private final AeroviaRepository aerovias;

    public MalhaAereaService(AeroportoRepository aeroportos, AeroviaRepository aerovias) {
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    @Override
    public List<Aeroporto> listarAeroportos() {
        return aeroportos.listarTodos();
    }

    @Override
    public List<Aerovia> listarAerovias() {
        return aerovias.listarTodas();
    }

    @Override
    public Aeroporto cadastrarAeroporto(String codigo, String nome, int quantidadeVagas) {
        Aeroporto aeroporto = new Aeroporto(codigo, nome, quantidadeVagas);
        if (aeroportos.buscarPorCodigo(codigo).isPresent()) {
            throw new OperacaoNaoPermitidaException("O aeroporto " + codigo + " já está cadastrado");
        }
        return aeroportos.salvar(aeroporto);
    }

    // §2: aerovia cadastrada não pode ser alterada, só criada com outro id
    @Override
    public Aerovia cadastrarAerovia(String id, String codigoOrigem, String codigoDestino, double distanciaKm) {
        if (aerovias.buscarPorId(id).isPresent()) {
            throw new OperacaoNaoPermitidaException("A aerovia " + id + " já está cadastrada e não pode ser alterada");
        }
        Aerovia aerovia = new Aerovia(id, buscarAeroporto(codigoOrigem).getCodigo(), buscarAeroporto(codigoDestino).getCodigo(),
                distanciaKm);
        return aerovias.salvar(aerovia);
    }

    private Aeroporto buscarAeroporto(String codigo) {
        return aeroportos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }
}
