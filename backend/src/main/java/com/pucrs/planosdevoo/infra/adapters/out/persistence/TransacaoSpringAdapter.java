package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.ports.out.TransacaoPort;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransacaoSpringAdapter implements TransacaoPort {

    private final TransactionTemplate template;

    public TransacaoSpringAdapter(PlatformTransactionManager transactionManager) {
        this.template = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T executar(Supplier<T> bloco) {
        return template.execute(status -> bloco.get());
    }
}
