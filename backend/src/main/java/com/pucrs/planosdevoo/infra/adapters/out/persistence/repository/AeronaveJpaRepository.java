package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeronaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeronaveJpaRepository extends JpaRepository<AeronaveEntity, String> {
}
