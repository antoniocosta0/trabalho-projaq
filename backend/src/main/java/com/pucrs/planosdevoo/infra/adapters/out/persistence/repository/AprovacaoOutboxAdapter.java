package com.pucrs.planosdevoo.infra.adapters.out.persistence.repository;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.AprovacaoPublisherPort;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.OutboxEventoEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

// a mensagem é gravada na mesma transação da aprovação e enviada depois (outbox)
@Component
public class AprovacaoOutboxAdapter implements AprovacaoPublisherPort {

    private final OutboxEventoJpaRepository outbox;

    public AprovacaoOutboxAdapter(OutboxEventoJpaRepository outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publicar(PlanoDeVoo plano, LocalDateTime aprovadoEm) {
        outbox.save(new OutboxEventoEntity(UUID.randomUUID().toString(), plano.getId(), aprovadoEm));
    }
}
