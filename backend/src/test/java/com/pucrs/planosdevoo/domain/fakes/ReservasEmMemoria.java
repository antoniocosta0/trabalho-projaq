package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.model.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.util.ArrayList;
import java.util.List;

// Repositório de teste: guarda as reservas numa lista, sem banco. Recusa
// reserva repetida como o UNIQUE do banco faria.
public class ReservasEmMemoria implements RepositorioReservas {

    public final List<ReservaAerovia> aerovias = new ArrayList<>();
    public final List<ReservaVaga> vagas = new ArrayList<>();

    @Override
    public List<ReservaAerovia> buscarReservasAerovia(String aeroviaId, List<Slot> slots) {
        return aerovias.stream()
                .filter(r -> r.getAerovia().getId().equals(aeroviaId) && slots.contains(r.getSlot()))
                .toList();
    }

    @Override
    public List<ReservaVaga> buscarReservasVaga(String aeroportoCodigo, List<Slot> slots) {
        return vagas.stream()
                .filter(r -> r.getAeroporto().getCodigo().equals(aeroportoCodigo) && slots.contains(r.getSlot()))
                .toList();
    }

    @Override
    public void reservar(List<ReservaAerovia> novasAerovias, List<ReservaVaga> novasVagas) {
        for (ReservaAerovia nova : novasAerovias) {
            boolean repetida = aerovias.stream().anyMatch(r -> r.getAerovia().getId().equals(nova.getAerovia().getId())
                    && r.getAltitude() == nova.getAltitude() && r.getSlot().equals(nova.getSlot()));
            if (repetida) {
                throw new ConflitoDeReservaException("altitude já reservada");
            }
            aerovias.add(nova);
        }
        for (ReservaVaga nova : novasVagas) {
            boolean repetida = vagas.stream().anyMatch(r -> r.getAeroporto().getCodigo().equals(nova.getAeroporto().getCodigo())
                    && r.getNumeroVaga() == nova.getNumeroVaga() && r.getSlot().equals(nova.getSlot()));
            if (repetida) {
                throw new ConflitoDeReservaException("vaga já reservada");
            }
            vagas.add(nova);
        }
    }

    @Override
    public void liberarReservasDoPlano(Long planoId) {
        aerovias.removeIf(r -> r.getPlanoId().equals(planoId));
        vagas.removeIf(r -> r.getPlanoId().equals(planoId));
    }
}
