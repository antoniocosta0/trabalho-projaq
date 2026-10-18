package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.DadosAeronave;
import com.pucrs.planosdevoo.domain.model.FabricaDeAeronaves;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeronaves;

public class CadastrarAeronaveUC {

    private final RepositorioAeronaves aeronaves;

    public CadastrarAeronaveUC(RepositorioAeronaves aeronaves) {
        this.aeronaves = aeronaves;
    }

    // A fábrica cria a subclasse da categoria e, com isso, valida os dados antes de gravar.
    public Aeronave executar(DadosAeronave dados) {
        FabricaDeAeronaves.criar(dados);
        if (aeronaves.buscarPorPrefixo(dados.prefixo()).isPresent()) {
            throw new OperacaoNaoPermitidaException("A aeronave " + dados.prefixo() + " já está cadastrada");
        }
        return aeronaves.salvar(dados);
    }
}
