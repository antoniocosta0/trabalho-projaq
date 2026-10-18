package com.pucrs.planosdevoo.domain.model;

import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Confere se as altitudes e as vagas que o plano pretende usar estão livres.
// Depende das reservas dos OUTROS planos, por isso não cabe dentro de
// PlanoDeVoo.validar(). Só consulta: nunca reserva nada.
public class VerificadorDeConflitos {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final RepositorioReservas reservas;

    public VerificadorDeConflitos(RepositorioReservas reservas) {
        this.reservas = reservas;
    }

    public List<String> verificar(PlanoDeVoo plano) {
        List<String> problemas = new ArrayList<>();
        // Slots que o próprio plano já usa em outro trecho: dois trechos do mesmo
        // plano não podem disputar a mesma altitude ou a mesma vaga.
        Set<String> usadosPeloPlano = new HashSet<>();

        for (Trecho t : plano.getTrechos()) {
            Aerovia aerovia = t.getAerovia();
            List<Slot> slotsAerovia = t.slotsOcupados();
            Set<Slot> ocupados = reservas.buscarReservasAerovia(aerovia.getId(), slotsAerovia).stream()
                    .filter(r -> r.getAltitude() == t.getAltitude())
                    .map(ReservaAerovia::getSlot)
                    .collect(Collectors.toSet());
            adicionar(problemas, "Trecho " + t.getOrdem() + ": a altitude " + t.getAltitude()
                            + " pés da aerovia " + aerovia.getId(),
                    slotsAerovia, ocupados, usadosPeloPlano, "A|" + aerovia.getId() + "|" + t.getAltitude());

            if (t.getPouso() != null) {
                Aeroporto aeroporto = aerovia.getDestino();
                int vaga = t.getPouso().getNumeroVaga();
                List<Slot> slotsVaga = t.slotsDaVaga();
                Set<Slot> vagaOcupada = reservas.buscarReservasVaga(aeroporto.getCodigo(), slotsVaga).stream()
                        .filter(r -> r.getNumeroVaga() == vaga)
                        .map(ReservaVaga::getSlot)
                        .collect(Collectors.toSet());
                adicionar(problemas, "Trecho " + t.getOrdem() + ": a vaga " + vaga + " de " + aeroporto.getCodigo(),
                        slotsVaga, vagaOcupada, usadosPeloPlano, "V|" + aeroporto.getCodigo() + "|" + vaga);
            }
        }
        return problemas;
    }

    // Uma mensagem por recurso e trecho, listando os slots em conflito: um slot
    // reservado por outro plano ou já usado por um trecho anterior deste plano.
    private static void adicionar(List<String> problemas, String recurso, List<Slot> slots, Set<Slot> ocupados,
                                  Set<String> usadosPeloPlano, String chave) {
        List<String> deOutroPlano = new ArrayList<>();
        List<String> doProprioPlano = new ArrayList<>();
        for (Slot slot : slots) {
            String hora = HORA.format(slot.getInicio());
            if (ocupados.contains(slot)) {
                deOutroPlano.add(hora);
            }
            if (!usadosPeloPlano.add(chave + "|" + slot.getInicio())) {
                doProprioPlano.add(hora);
            }
        }
        if (!deOutroPlano.isEmpty()) {
            problemas.add(recurso + " já está reservada no(s) slot(s) das " + String.join(", ", deOutroPlano));
        }
        if (!doProprioPlano.isEmpty()) {
            problemas.add(recurso + " já é usada por outro trecho deste plano no(s) slot(s) das "
                    + String.join(", ", doProprioPlano));
        }
    }
}
