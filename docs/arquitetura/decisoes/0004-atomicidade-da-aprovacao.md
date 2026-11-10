# ADR 0004 — Atomicidade da aprovação

**Decisão.** Aprovar e cancelar rodam dentro de `TransacaoPort.executar(...)`, implementada
por `TransacaoSpringAdapter` com `TransactionTemplate`. A aprovação confere a disponibilidade
de novo dentro da transação, e as tabelas de reserva têm `UNIQUE` por recurso e slot.

**Alternativas.**
- `@Transactional` no serviço: mais simples, mas põe Spring no domínio (ADR 0001).
- Lock pessimista nas reservas: fecha a disputa entre aprovações simultâneas, mas é mais
  complexo e o `UNIQUE` já cobre o caso.

**Justificativa.** Atende ao §5.4 e ao §6.5: ou tudo é gravado, ou nada. Se duas aprovações
passarem juntas pela conferência, o `UNIQUE` derruba a segunda e a transação desfaz tudo.
