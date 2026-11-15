package com.pucrs.registro.infra.adapters.out.persistence.repository;

import com.pucrs.registro.infra.adapters.out.persistence.entity.RegistroAprovacaoEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAprovacaoJpaRepository extends JpaRepository<RegistroAprovacaoEntity, String> {

    boolean existsByEventoIdOrPlanoId(String eventoId, Long planoId);

    Optional<RegistroAprovacaoEntity> findByPlanoId(Long planoId);

    List<RegistroAprovacaoEntity> findAllByOrderByRecebidoEm();
}
