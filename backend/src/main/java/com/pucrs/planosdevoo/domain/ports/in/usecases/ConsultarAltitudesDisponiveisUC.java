package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// UC07 — Consultar altitudes disponíveis em uma aerovia por período. Só consulta: não reserva.
public class ConsultarAltitudesDisponiveisUC {

    private final AeroviaRepository aerovias;
    private final ReservaRepository reservas;

    public ConsultarAltitudesDisponiveisUC(AeroviaRepository aerovias, ReservaRepository reservas) {
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
