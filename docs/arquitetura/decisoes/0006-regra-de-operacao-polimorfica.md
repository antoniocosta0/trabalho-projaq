# ADR 0006 — Regra de operação por categoria de aeronave

**Decisão.** `Aeronave` é uma classe `sealed` com uma subclasse por categoria, e cada uma
implementa `podeOperar(altitude, entrada, saida)`. `AeronaveFactory` cria a subclasse a partir
da categoria.

**Alternativas.**
- Um `switch` por categoria dentro da validação do plano: a validação mudaria a cada categoria.
- Regras configuráveis em banco: flexível, mas desnecessário para três categorias.

**Justificativa.** O §3 avisa que novas categorias podem surgir. Uma categoria nova é uma
subclasse, um `case` na fábrica e os `switch` sobre o tipo `sealed` nos mappers, que o
compilador aponta. A validação do plano não muda.
