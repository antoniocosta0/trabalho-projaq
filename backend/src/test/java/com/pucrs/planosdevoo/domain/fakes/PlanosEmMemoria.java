package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Repositório de teste: o plano guarda o que foi salvo, sem banco. O id vem
// de quem monta o teste (PlanoDeVoo.reconstituir).
public class PlanosEmMemoria implements RepositorioPlanosDeVoo {

    private final List<PlanoDeVoo> planos = new ArrayList<>();

    public void adicionar(PlanoDeVoo plano) {
        planos.add(plano);
    }

    @Override
    public PlanoDeVoo salvar(PlanoDeVoo plano) {
        planos.removeIf(p -> p.getId().equals(plano.getId()));
        planos.add(plano);
        return plano;
    }

    @Override
    public Optional<PlanoDeVoo> buscarPorId(Long id) {
        return planos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public List<PlanoDeVoo> listarTodos() {
        return List.copyOf(planos);
    }

    @Override
    public List<PlanoDeVoo> listarPorSituacao(SituacaoPlano situacao) {
        return planos.stream().filter(p -> p.getSituacao() == situacao).toList();
    }
}
