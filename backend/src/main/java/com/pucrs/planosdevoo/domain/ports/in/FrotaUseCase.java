package com.pucrs.planosdevoo.domain.ports.in;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Piloto;
import java.util.List;

public interface FrotaUseCase {

    List<Piloto> listarPilotos();

    List<Aeronave> listarAeronaves();

    Piloto cadastrarPiloto(String nome);

    Aeronave cadastrarAeronave(CadastrarAeronaveCommand comando);
}
