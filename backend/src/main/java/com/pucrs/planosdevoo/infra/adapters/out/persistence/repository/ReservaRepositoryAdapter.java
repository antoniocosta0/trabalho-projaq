package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.exceptions.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.ReservaAerovia;
import com.pucrs.planosdevoo.domain.model.ReservaVaga;
import com.pucrs.planosdevoo.domain.model.Slot;
import com.pucrs.planosdevoo.domain.ports.out.ReservaRepository;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.ReservaAeroviaEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.ReservaVagaEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.mapper.ReservaMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class ReservaRepositoryAdapter implements ReservaRepository {

    private final ReservaAeroviaJpaRepository reservasAerovia;
    private final ReservaVagaJpaRepository reservasVaga;

    public ReservaRepositoryAdapter(ReservaAeroviaJpaRepository reservasAerovia,
                                    ReservaVagaJpaRepository reservasVaga) {
        this.reservasAerovia = reservasAerovia;
        this.reservasVaga = reservasVaga;
    }

    @Override
    public List<ReservaAerovia> buscarReservasAerovia(String aeroviaId, List<Slot> slots) {
        return reservasAerovia.findByAeroviaIdAndSlotInicioIn(aeroviaId, inicios(slots)).stream()
                .map(ReservaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ReservaVaga> buscarReservasVaga(String aeroportoCodigo, List<Slot> slots) {
        return reservasVaga.findByAeroportoCodigoAndSlotInicioIn(aeroportoCodigo, inicios(slots)).stream()
                .map(ReservaMapper::paraDominio)
                .toList();
    }

    // flush para o UNIQUE falhar aqui, dentro da transação, e não só no commit
    @Override
    public void reservar(List<ReservaAerovia> aerovia, List<ReservaVaga> vagas) {
        try {
            reservasAerovia.saveAllAndFlush(aerovia.stream()
                    .map(r -> new ReservaAeroviaEntity(r.getPlanoId(),
                            r.getAeroviaId(), r.getAltitude(),
                            r.getSlot().getInicio()))
                    .toList());
            reservasVaga.saveAllAndFlush(vagas.stream()
                    .map(r -> new ReservaVagaEntity(r.getPlanoId(),
                            r.getAeroportoCodigo(), r.getNumeroVaga(),
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
