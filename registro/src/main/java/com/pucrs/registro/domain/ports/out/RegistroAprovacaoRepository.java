package com.pucrs.registro.domain.ports.out;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import java.util.List;
import java.util.Optional;

public interface RegistroAprovacaoRepository {

    boolean existe(String eventoId, Long planoId);

    void salvar(RegistroAprovacao registro);

    List<RegistroAprovacao> listarTodos();

    Optional<RegistroAprovacao> buscarPorPlano(Long planoId);
}
