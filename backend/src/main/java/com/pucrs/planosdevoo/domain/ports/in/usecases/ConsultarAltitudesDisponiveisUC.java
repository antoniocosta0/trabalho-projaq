package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAerovias;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// UC07 — Consultar altitudes disponíveis em uma aerovia por período. Só consulta: não reserva.
public class ConsultarAltitudesDisponiveisUC {

    private final RepositorioAerovias aerovias;
    private final RepositorioReservas reservas;

    public ConsultarAltitudesDisponiveisUC(RepositorioAerovias aerovias, RepositorioReservas reservas) {
        this.aerovias = aerovias;
        this.reservas = reservas;
    }

    // Disponível = livre em TODOS os slots do período [inicio, fim).
    public List<Integer> executar(String aeroviaId, LocalDateTime inicio, LocalDateTime fim) {
        aerovias.buscarPorId(aeroviaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + aeroviaId + " não encontrada"));
        List<Slot> slots = Slot.entre(inicio, fim);
        Set<Integer> ocupadas = reservas.buscarReservasAerovia(aeroviaId, slots).stream()
                .map(ReservaAerovia::getAltitude)
                .collect(Collectors.toSet());
        return Trecho.altitudesPermitidas().stream().filter(a -> !ocupadas.contains(a)).toList();
    }
}
