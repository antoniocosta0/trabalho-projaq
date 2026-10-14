package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AeronaveSpringDataRepository extends JpaRepository<AeronaveJpaEntity, String> {
}
