package com.pucrs.cadastro.infra.adapters.out.persistence.repository;

import com.pucrs.cadastro.infra.adapters.out.persistence.entity.AeroviaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeroviaJpaRepository extends JpaRepository<AeroviaEntity, String> {
}
