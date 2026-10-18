package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaVagaSpringDataRepository extends JpaRepository<ReservaVagaJpaEntity, Long> {

    List<ReservaVagaJpaEntity> findByAeroporto_CodigoAndSlotInicioIn(String aeroportoCodigo, Collection<LocalDateTime> slots);

    @Modifying
    @Query("delete from ReservaVagaJpaEntity r where r.planoId = :planoId")
    void deleteByPlanoId(@Param("planoId") Long planoId);
}
