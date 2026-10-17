package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.Pouso;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioPlanosDeVoo;

// UC06 — Definir ou alterar o percurso. As regras (só em edição, ordem dos
// trechos, velocidade da aeronave) ficam no PlanoDeVoo; aqui só se busca o que
// a requisição referencia por identificador e se salva o resultado.
public class DefinirPercursoUC {

    private final RepositorioPlanosDeVoo planos;
    private final RepositorioAerovias aerovias;

    public DefinirPercursoUC(RepositorioPlanosDeVoo planos, RepositorioAerovias aerovias) {
        this.planos = planos;
        this.aerovias = aerovias;
    }

    public PlanoDeVoo adicionarTrecho(Long planoId, TrechoCommand comando) {
        PlanoDeVoo plano = buscarPlano(planoId);
        plano.adicionarTrecho(buscarAerovia(comando.aeroviaId()), comando.altitude(),
                comando.horarioEntrada(), pousoDe(comando));
        return planos.salvar(plano);
    }

    public PlanoDeVoo alterarTrecho(Long planoId, int ordem, TrechoCommand comando) {
        PlanoDeVoo plano = buscarPlano(planoId);
        plano.alterarTrecho(ordem, buscarAerovia(comando.aeroviaId()), comando.altitude(),
                comando.horarioEntrada(), pousoDe(comando));
        return planos.salvar(plano);
    }

    public PlanoDeVoo removerTrecho(Long planoId, int ordem) {
        PlanoDeVoo plano = buscarPlano(planoId);
        plano.removerTrecho(ordem);
        return planos.salvar(plano);
    }

    private PlanoDeVoo buscarPlano(Long id) {
        return planos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano " + id + " não encontrado"));
    }

    private Aerovia buscarAerovia(String id) {
        return aerovias.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + id + " não encontrada"));
    }

    private static Pouso pousoDe(TrechoCommand c) {
        if (c.numeroVaga() == null && c.permanenciaMinutos() == null) {
            return null;
        }
        if (c.numeroVaga() == null || c.permanenciaMinutos() == null) {
            throw new IllegalArgumentException("Para pousar, informe a vaga e o tempo de permanência");
        }
        return new Pouso(c.numeroVaga(), c.permanenciaMinutos());
    }
}
