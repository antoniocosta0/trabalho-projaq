package com.pucrs.planosdevoo.domain.ports.in.usecases;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioAeroportos;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// UC08 — Consultar vagas disponíveis em um aeroporto por período. Só consulta: não reserva.
public class ConsultarVagasDisponiveisUC {

    private final RepositorioAeroportos aeroportos;
    private final RepositorioReservas reservas;

    public ConsultarVagasDisponiveisUC(RepositorioAeroportos aeroportos, RepositorioReservas reservas) {
        this.aeroportos = aeroportos;
        this.reservas = reservas;
    }

    // Disponível = livre em TODOS os slots do período [inicio, fim).
    public List<Integer> executar(String aeroportoCodigo, LocalDateTime inicio, LocalDateTime fim) {
        Aeroporto aeroporto = aeroportos.buscarPorCodigo(aeroportoCodigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + aeroportoCodigo + " não encontrado"));
        List<Slot> slots = Slot.entre(inicio, fim);
        Set<Integer> ocupadas = reservas.buscarReservasVaga(aeroportoCodigo, slots).stream()
                .map(ReservaVaga::getNumeroVaga)
                .collect(Collectors.toSet());
        return IntStream.rangeClosed(1, aeroporto.getQuantidadeVagas())
                .filter(vaga -> !ocupadas.contains(vaga))
                .boxed()
                .toList();
    }
}
