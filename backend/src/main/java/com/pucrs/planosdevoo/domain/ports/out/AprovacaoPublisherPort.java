package com.pucrs.planosdevoo.domain.ports.out;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import java.time.LocalDateTime;

public interface AprovacaoPublisherPort {

    void publicar(PlanoDeVoo plano, LocalDateTime aprovadoEm);
}
