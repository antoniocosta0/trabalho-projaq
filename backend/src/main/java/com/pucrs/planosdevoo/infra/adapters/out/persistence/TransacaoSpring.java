package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.ports.out.Transacao;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

// Os casos de uso não têm anotação do Spring, então a transação é aberta aqui,
// por programação: exceção dentro do bloco = rollback.
@Component
public class TransacaoSpring implements Transacao {

    private final TransactionTemplate template;

    public TransacaoSpring(PlatformTransactionManager transactionManager) {
        this.template = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T executar(Supplier<T> bloco) {
        return template.execute(status -> bloco.get());
    }
}
