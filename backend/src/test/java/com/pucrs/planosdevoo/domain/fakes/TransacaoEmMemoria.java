package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.ports.out.TransacaoPort;
import java.util.function.Supplier;

public class TransacaoEmMemoria implements TransacaoPort {

    @Override
    public <T> T executar(Supplier<T> bloco) {
        return bloco.get();
    }
}
