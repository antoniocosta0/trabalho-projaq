package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import java.util.List;

public interface ReservaRepository {

    List<ReservaAerovia> buscarReservasAerovia(String aeroviaId, List<Slot> slots);

    List<ReservaVaga> buscarReservasVaga(String aeroportoCodigo, List<Slot> slots);

    void reservar(List<ReservaAerovia> aerovias, List<ReservaVaga> vagas);

    void liberarReservasDoPlano(Long planoId);
}
