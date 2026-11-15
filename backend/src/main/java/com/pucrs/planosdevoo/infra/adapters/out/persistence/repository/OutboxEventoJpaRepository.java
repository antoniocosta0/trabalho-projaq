package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.OutboxEventoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventoJpaRepository extends JpaRepository<OutboxEventoEntity, String> {

    List<OutboxEventoEntity> findByPublicadoEmIsNullOrderByAprovadoEm();
}
