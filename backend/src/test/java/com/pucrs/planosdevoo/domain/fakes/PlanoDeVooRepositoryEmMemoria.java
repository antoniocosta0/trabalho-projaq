package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.out.PlanoDeVooRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanoDeVooRepositoryEmMemoria implements PlanoDeVooRepository {

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
    public List<PlanoDeVoo> listarPorCriador(String criador) {
        return planos.stream().filter(p -> p.pertenceA(criador)).toList();
    }

    @Override
    public List<PlanoDeVoo> listarPorCriadorESituacao(String criador, SituacaoPlano situacao) {
        return listarPorCriador(criador).stream().filter(p -> p.getSituacao() == situacao).toList();
    }
}
