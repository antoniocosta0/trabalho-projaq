package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import java.util.List;
import java.util.Optional;

public interface RepositorioPlanosDeVoo {
    PlanoDeVoo salvar(PlanoDeVoo plano);
    Optional<PlanoDeVoo> buscarPorId(Long id);
    List<PlanoDeVoo> listarTodos();
    List<PlanoDeVoo> listarPorSituacao(SituacaoPlano situacao);
}
