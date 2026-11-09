package com.pucrs.planosdevoo.domain.services;

import static java.util.stream.Collectors.toSet;

import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.model.Trecho;
import com.pucrs.planosdevoo.domain.ports.in.DisponibilidadeUseCase;
import com.pucrs.planosdevoo.domain.ports.out.AeroportoRepository;
import com.pucrs.planosdevoo.domain.ports.out.AeroviaRepository;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class DisponibilidadeService implements DisponibilidadeUseCase {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final AeroviaRepository aerovias;
    private final AeroportoRepository aeroportos;
    private final ReservaRepository reservas;

    public DisponibilidadeService(AeroviaRepository aerovias, AeroportoRepository aeroportos,
                                  ReservaRepository reservas) {
        this.aerovias = aerovias;
        this.aeroportos = aeroportos;
        this.reservas = reservas;
    }

    @Override
    public List<Integer> altitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim) {
        aerovias.buscarPorId(aeroviaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + aeroviaId + " não encontrada"));
        Set<Integer> ocupadas = reservas.buscarReservasAerovia(aeroviaId, Slot.entre(inicio, fim)).stream()
                .map(ReservaAerovia::getAltitude)
                .collect(toSet());
        return Trecho.altitudesPermitidas().stream().filter(a -> !ocupadas.contains(a)).toList();
    }

    @Override
    public List<Integer> vagasDisponiveis(String codigoAeroporto, LocalDateTime inicio, LocalDateTime fim) {
        Aeroporto aeroporto = aeroportos.buscarPorCodigo(codigoAeroporto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigoAeroporto + " não encontrado"));
        Set<Integer> ocupadas = reservas.buscarReservasVaga(codigoAeroporto, Slot.entre(inicio, fim)).stream()
                .map(ReservaVaga::getNumeroVaga)
                .collect(toSet());
        return IntStream.rangeClosed(1, aeroporto.getQuantidadeVagas())
                .filter(vaga -> !ocupadas.contains(vaga))
                .boxed()
                .toList();
    }

    public List<String> conflitos(PlanoDeVoo plano) {
        List<String> problemas = new ArrayList<>();
        Set<String> usadosPeloPlano = new HashSet<>();
        for (Trecho trecho : plano.getTrechos()) {
            verificarAltitude(trecho, usadosPeloPlano, problemas);
            if (trecho.getPouso() != null) {
                verificarVaga(trecho, usadosPeloPlano, problemas);
            }
        }
        return problemas;
    }

    private void verificarAltitude(Trecho trecho, Set<String> usadosPeloPlano, List<String> problemas) {
        String aeroviaId = trecho.getAerovia().getId();
        Set<Slot> reservados = reservas.buscarReservasAerovia(aeroviaId, trecho.slotsOcupados()).stream()
                .filter(r -> r.getAltitude() == trecho.getAltitude())
                .map(ReservaAerovia::getSlot)
                .collect(toSet());
        String recurso = "a altitude " + trecho.getAltitude() + " pés da aerovia " + aeroviaId;
        verificarSlots(trecho, recurso, trecho.slotsOcupados(), reservados, usadosPeloPlano, problemas);
    }

    private void verificarVaga(Trecho trecho, Set<String> usadosPeloPlano, List<String> problemas) {
        String codigo = trecho.getAerovia().getDestino().getCodigo();
        int vaga = trecho.getPouso().getNumeroVaga();
        Set<Slot> reservados = reservas.buscarReservasVaga(codigo, trecho.slotsDaVaga()).stream()
                .filter(r -> r.getNumeroVaga() == vaga)
                .map(ReservaVaga::getSlot)
                .collect(toSet());
        String recurso = "a vaga " + vaga + " de " + codigo;
        verificarSlots(trecho, recurso, trecho.slotsDaVaga(), reservados, usadosPeloPlano, problemas);
    }

    private static void verificarSlots(Trecho trecho, String recurso, List<Slot> slots, Set<Slot> reservados,
                                       Set<String> usadosPeloPlano, List<String> problemas) {
        List<String> ocupados = new ArrayList<>();
        for (Slot slot : slots) {
            boolean usadoPorOutroTrecho = !usadosPeloPlano.add(recurso + slot.getInicio());
            if (reservados.contains(slot) || usadoPorOutroTrecho) {
                ocupados.add(HORA.format(slot.getInicio()));
            }
        }
        if (!ocupados.isEmpty()) {
            problemas.add("Trecho " + trecho.getOrdem() + ": " + recurso + " está ocupada no(s) slot(s) das "
                    + String.join(", ", ocupados));
        }
    }
}
