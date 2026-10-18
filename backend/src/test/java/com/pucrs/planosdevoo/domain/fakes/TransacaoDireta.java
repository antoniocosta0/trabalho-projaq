package com.pucrs.planosdevoo.domain.fakes;

import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import java.util.function.Supplier;

// Sem banco não há o que desfazer: só executa o bloco. O rollback de verdade
// é testado com o banco, em AprovacaoAtomicaTest.
public class TransacaoDireta implements Transacao {

    @Override
    public <T> T executar(Supplier<T> bloco) {
        return bloco.get();
    }
}
