package com.pucrs.cadastro.infra.adapters.out.persistence.repository;

import com.pucrs.cadastro.infra.adapters.out.persistence.entity.AeroportoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeroportoJpaRepository extends JpaRepository<AeroportoEntity, String> {
}
