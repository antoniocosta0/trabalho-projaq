package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.ports.out.AprovacaoPublisherPort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AprovacaoPublisherEmMemoria implements AprovacaoPublisherPort {

    public final List<Long> publicados = new ArrayList<>();

    @Override
    public void publicar(PlanoDeVoo plano, LocalDateTime aprovadoEm) {
        publicados.add(plano.getId());
    }
}
