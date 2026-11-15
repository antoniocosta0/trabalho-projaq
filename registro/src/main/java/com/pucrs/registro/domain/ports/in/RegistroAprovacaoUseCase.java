package com.pucrs.registro.domain.ports.in;

import com.pucrs.registro.domain.model.RegistroAprovacao;
import java.util.List;

public interface RegistroAprovacaoUseCase {

    boolean registrar(RegistroAprovacao registro);

    List<RegistroAprovacao> listar();

    RegistroAprovacao buscarPorPlano(Long planoId);
}
