package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoDeVooSpringDataRepository extends JpaRepository<PlanoDeVooJpaEntity, Long> {
    List<PlanoDeVooJpaEntity> findBySituacao(SituacaoPlano situacao);
}
