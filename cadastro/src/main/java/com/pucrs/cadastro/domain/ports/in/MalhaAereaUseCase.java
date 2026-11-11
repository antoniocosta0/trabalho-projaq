package com.pucrs.cadastro.domain.ports.in;

import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.model.Aerovia;
import java.util.List;

public interface MalhaAereaUseCase {

    List<Aeroporto> listarAeroportos();

    Aeroporto buscarAeroporto(String codigo);

    Aeroporto cadastrarAeroporto(String codigo, String nome, int quantidadeVagas);

    List<Aerovia> listarAerovias(String origem, String destino);

    Aerovia buscarAerovia(String id);

    Aerovia cadastrarAerovia(String id, String origem, String destino, double distanciaKm);
}
