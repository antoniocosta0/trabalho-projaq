package com.pucrs.registro.domain.services;

import com.pucrs.registro.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.registro.domain.model.RegistroAprovacao;
import com.pucrs.registro.domain.ports.in.RegistroAprovacaoUseCase;
import com.pucrs.registro.domain.ports.out.RegistroAprovacaoRepository;
import java.util.List;

public class RegistroAprovacaoService implements RegistroAprovacaoUseCase {

    private final RegistroAprovacaoRepository registros;

    public RegistroAprovacaoService(RegistroAprovacaoRepository registros) {
        this.registros = registros;
    }

    // a mesma aprovação pode chegar mais de uma vez (reenvio, reentrega): só a primeira é gravada
    @Override
    public boolean registrar(RegistroAprovacao registro) {
        if (registros.existe(registro.getEventoId(), registro.getPlanoId())) {
            return false;
        }
        registros.salvar(registro);
        return true;
    }

    @Override
    public List<RegistroAprovacao> listar() {
        return registros.listarTodos();
    }

    @Override
    public RegistroAprovacao buscarPorPlano(Long planoId) {
        return registros.buscarPorPlano(planoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma aprovação registrada para o plano " + planoId));
    }
}
