package com.pucrs.planosdevoo.domain.ports.out;

import java.util.function.Supplier;

public interface TransacaoPort {

    <T> T executar(Supplier<T> bloco);
}
