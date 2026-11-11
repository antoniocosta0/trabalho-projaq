package com.pucrs.planosdevoo.domain.ports.in;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import java.util.List;

public interface PlanoDeVooUseCase {

    PlanoDeVoo criar(String usuario, PlanoCommand comando);

    PlanoDeVoo alterar(String usuario, Long id, PlanoCommand comando);

    PlanoDeVoo buscar(String usuario, Long id);

    List<PlanoDeVoo> listar(String usuario, SituacaoPlano situacao);

    PlanoDeVoo adicionarTrecho(String usuario, Long planoId, TrechoCommand comando);

    PlanoDeVoo alterarTrecho(String usuario, Long planoId, int ordem, TrechoCommand comando);

    PlanoDeVoo removerTrecho(String usuario, Long planoId, int ordem);

    List<String> validar(String usuario, Long planoId);

    PlanoDeVoo aprovar(String usuario, Long planoId);

    PlanoDeVoo cancelar(String usuario, Long planoId);

    PlanoDeVoo abandonar(String usuario, Long planoId);
}
