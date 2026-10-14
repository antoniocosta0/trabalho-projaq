package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotoSpringDataRepository extends JpaRepository<PilotoJpaEntity, Long> {
}
