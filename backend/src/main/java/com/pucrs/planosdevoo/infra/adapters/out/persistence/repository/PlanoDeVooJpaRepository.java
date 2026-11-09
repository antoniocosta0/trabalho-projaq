package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PlanoDeVooEntity;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoDeVooJpaRepository extends JpaRepository<PlanoDeVooEntity, Long> {
    List<PlanoDeVooEntity> findBySituacao(SituacaoPlano situacao);
}
