package com.pucrs.planosdevoo.domain.ports.out.repositories;

import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import java.util.List;

public interface RepositorioReservas {

    // Reservas da aerovia que ocupam pelo menos um dos slots, em qualquer altitude.
    List<ReservaAerovia> buscarReservasAerovia(String aeroviaId, List<Slot> slots);

    // Reservas do aeroporto que ocupam pelo menos um dos slots, em qualquer vaga.
    List<ReservaVaga> buscarReservasVaga(String aeroportoCodigo, List<Slot> slots);

    // Grava as duas listas. Lança ConflitoDeReservaException se algum slot já
    // tiver dono; quem chama decide o que desfazer (ver Transacao).
    void reservar(List<ReservaAerovia> aerovias, List<ReservaVaga> vagas);

    void liberarReservasDoPlano(Long planoId);
}
