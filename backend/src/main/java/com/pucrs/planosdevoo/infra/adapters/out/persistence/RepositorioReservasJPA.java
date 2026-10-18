package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.repositories.RepositorioReservas;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioReservasJPA implements RepositorioReservas {

    private final ReservaAeroviaSpringDataRepository reservasAerovia;
    private final ReservaVagaSpringDataRepository reservasVaga;
    private final AeroviaSpringDataRepository aerovias;
    private final AeroportoSpringDataRepository aeroportos;

    public RepositorioReservasJPA(ReservaAeroviaSpringDataRepository reservasAerovia,
                                  ReservaVagaSpringDataRepository reservasVaga,
                                  AeroviaSpringDataRepository aerovias, AeroportoSpringDataRepository aeroportos) {
        this.reservasAerovia = reservasAerovia;
        this.reservasVaga = reservasVaga;
        this.aerovias = aerovias;
        this.aeroportos = aeroportos;
    }

    @Override
    public List<ReservaAerovia> buscarReservasAerovia(String aeroviaId, List<Slot> slots) {
        return reservasAerovia.findByAerovia_IdAndSlotInicioIn(aeroviaId, inicios(slots)).stream()
                .map(ReservasMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ReservaVaga> buscarReservasVaga(String aeroportoCodigo, List<Slot> slots) {
        return reservasVaga.findByAeroporto_CodigoAndSlotInicioIn(aeroportoCodigo, inicios(slots)).stream()
                .map(ReservasMapper::paraDominio)
                .toList();
    }

    // O flush força o banco a conferir os UNIQUE agora, dentro da transação do
    // chamador, e não só no commit. Se der conflito, a exceção desfaz tudo.
    @Override
    public void reservar(List<ReservaAerovia> aerovia, List<ReservaVaga> vagas) {
        try {
            reservasAerovia.saveAllAndFlush(aerovia.stream()
                    .map(r -> new ReservaAeroviaJpaEntity(r.getPlanoId(),
                            aerovias.getReferenceById(r.getAerovia().getId()), r.getAltitude(),
                            r.getSlot().getInicio()))
                    .toList());
            reservasVaga.saveAllAndFlush(vagas.stream()
                    .map(r -> new ReservaVagaJpaEntity(r.getPlanoId(),
                            aeroportos.getReferenceById(r.getAeroporto().getCodigo()), r.getNumeroVaga(),
                            r.getSlot().getInicio()))
                    .toList());
        } catch (DataIntegrityViolationException e) {
            throw new ConflitoDeReservaException(
                    "Uma altitude ou vaga do plano acabou de ser reservada por outro plano");
        }
    }

    @Override
    public void liberarReservasDoPlano(Long planoId) {
        reservasAerovia.deleteByPlanoId(planoId);
        reservasVaga.deleteByPlanoId(planoId);
    }

    private static List<LocalDateTime> inicios(List<Slot> slots) {
        return slots.stream().map(Slot::getInicio).toList();
    }
}
