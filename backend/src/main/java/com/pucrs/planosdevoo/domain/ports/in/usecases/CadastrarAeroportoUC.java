package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;

// Não é caso de uso numerado (ADR 0009): a malha vem da carga inicial, mas o
// enunciado (§2 e §8.1) pede que dê para cadastrar aeroporto novo sem mexer no código.
public class CadastrarAeroportoUC {

    private final RepositorioAeroportos aeroportos;

    public CadastrarAeroportoUC(RepositorioAeroportos aeroportos) {
        this.aeroportos = aeroportos;
    }

    public Aeroporto executar(String codigo, String nome, int quantidadeVagas) {
        Aeroporto aeroporto = new Aeroporto(codigo, nome, quantidadeVagas);
        if (aeroportos.buscarPorCodigo(codigo).isPresent()) {
            throw new OperacaoNaoPermitidaException("O aeroporto " + codigo + " já está cadastrado");
        }
        return aeroportos.salvar(aeroporto);
    }
}
