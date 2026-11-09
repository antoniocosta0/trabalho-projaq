package com.pucrs.planosdevoo.domain.ports.in;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import java.util.List;

public interface MalhaAereaUseCase {

    List<Aeroporto> listarAeroportos();

    List<Aerovia> listarAerovias();

    Aeroporto cadastrarAeroporto(String codigo, String nome, int quantidadeVagas);

    Aerovia cadastrarAerovia(String id, String codigoOrigem, String codigoDestino, double distanciaKm);
}
