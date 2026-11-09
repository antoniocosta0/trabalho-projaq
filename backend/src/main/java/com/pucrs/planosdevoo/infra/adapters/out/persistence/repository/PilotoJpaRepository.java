package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PilotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotoJpaRepository extends JpaRepository<PilotoEntity, Long> {
}
