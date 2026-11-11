package com.pucrs.cadastro.domain.services;

import com.pucrs.cadastro.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.cadastro.domain.exceptions.RegistroDuplicadoException;
import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.model.Aerovia;
import com.pucrs.cadastro.domain.ports.in.MalhaAereaUseCase;
import com.pucrs.cadastro.domain.ports.out.AeroportoRepository;
import com.pucrs.cadastro.domain.ports.out.AeroviaRepository;
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
    public Aeroporto buscarAeroporto(String codigo) {
        return aeroportos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }

    @Override
    public Aeroporto cadastrarAeroporto(String codigo, String nome, int quantidadeVagas) {
        Aeroporto aeroporto = Aeroporto.comVagas(codigo, nome, quantidadeVagas);
        if (aeroportos.buscarPorCodigo(codigo).isPresent()) {
            throw new RegistroDuplicadoException("O aeroporto " + codigo + " já está cadastrado");
        }
        return aeroportos.salvar(aeroporto);
    }

    @Override
    public List<Aerovia> listarAerovias(String origem, String destino) {
        return aerovias.listarTodas().stream()
                .filter(a -> origem == null || a.getOrigem().equals(origem))
                .filter(a -> destino == null || a.getDestino().equals(destino))
                .toList();
    }

    @Override
    public Aerovia buscarAerovia(String id) {
        return aerovias.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + id + " não encontrada"));
    }

    // §2: aerovia cadastrada não pode ser alterada, só criada com outro id
    @Override
    public Aerovia cadastrarAerovia(String id, String origem, String destino, double distanciaKm) {
        if (aerovias.buscarPorId(id).isPresent()) {
            throw new RegistroDuplicadoException("A aerovia " + id + " já está cadastrada e não pode ser alterada");
        }
        buscarAeroporto(origem);
        buscarAeroporto(destino);
        return aerovias.salvar(new Aerovia(id, origem, destino, distanciaKm));
    }
}
