# ADR 0005 — Slot como value object

**Decisão.** `Slot` representa uma hora cheia. `Slot.entre(inicio, fim)` devolve os slots que
um intervalo `[inicio, fim)` ocupa. Reservas são gravadas uma por slot.

**Alternativas.**
- Guardar intervalos e comparar sobreposição: menos linhas no banco, mas a regra de slot
  inteiro (§5.1) ficaria espalhada em consultas.

**Justificativa.** A regra de ocupação fica num lugar só, é testável isoladamente e o conflito
vira igualdade de slot, que o `UNIQUE` do banco consegue garantir.
