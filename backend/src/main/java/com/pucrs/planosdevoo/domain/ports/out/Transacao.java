package com.pucrs.planosdevoo.domain.ports.out;

import java.util.function.Supplier;

// Tudo o que o bloco gravar vale junto ou não vale: se ele lançar exceção, o
// adaptador desfaz as gravações. O domínio não conhece a tecnologia que faz isso.
public interface Transacao {
    <T> T executar(Supplier<T> bloco);
}
