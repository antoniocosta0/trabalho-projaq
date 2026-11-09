package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.ReservaAeroviaEntity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaAeroviaJpaRepository extends JpaRepository<ReservaAeroviaEntity, Long> {

    List<ReservaAeroviaEntity> findByAerovia_IdAndSlotInicioIn(String aeroviaId, Collection<LocalDateTime> slots);

    @Modifying
    @Query("delete from ReservaAeroviaEntity r where r.planoId = :planoId")
    void deleteByPlanoId(@Param("planoId") Long planoId);
}
