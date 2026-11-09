package com.pucrs.planosdevoo.domain.ports.in;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import java.util.List;

public interface PlanoDeVooUseCase {

    PlanoDeVoo criar(PlanoCommand comando);

    PlanoDeVoo alterar(Long id, PlanoCommand comando);

    PlanoDeVoo buscar(Long id);

    List<PlanoDeVoo> listar(SituacaoPlano situacao);

    PlanoDeVoo adicionarTrecho(Long planoId, TrechoCommand comando);

    PlanoDeVoo alterarTrecho(Long planoId, int ordem, TrechoCommand comando);

    PlanoDeVoo removerTrecho(Long planoId, int ordem);

    List<String> validar(Long planoId);

    PlanoDeVoo aprovar(Long planoId);

    PlanoDeVoo cancelar(Long planoId);

    PlanoDeVoo abandonar(Long planoId);
}
