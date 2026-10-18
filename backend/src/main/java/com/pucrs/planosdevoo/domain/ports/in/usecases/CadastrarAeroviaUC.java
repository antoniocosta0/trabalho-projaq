package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;

// Aerovia é imutável: só se cadastra nova, com id novo. Id repetido é recusado,
// e não tratado como alteração da existente.
public class CadastrarAeroviaUC {

    private final RepositorioAerovias aerovias;
    private final RepositorioAeroportos aeroportos;

    public CadastrarAeroviaUC(RepositorioAerovias aerovias, RepositorioAeroportos aeroportos) {
        this.aerovias = aerovias;
        this.aeroportos = aeroportos;
    }

    public Aerovia executar(String id, String codigoOrigem, String codigoDestino, double distanciaKm) {
        if (aerovias.buscarPorId(id).isPresent()) {
            throw new OperacaoNaoPermitidaException("A aerovia " + id
                    + " já está cadastrada, e aerovia não pode ser alterada: cadastre uma nova, com outro id");
        }
        Aerovia aerovia = new Aerovia(id, buscarAeroporto(codigoOrigem), buscarAeroporto(codigoDestino), distanciaKm);
        return aerovias.salvar(aerovia);
    }

    private Aeroporto buscarAeroporto(String codigo) {
        return aeroportos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado"));
    }
}
