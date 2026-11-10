# ADR 0007 — Aeronaves em tabela única

**Decisão.** Todas as categorias ficam na tabela `aeronave`, com a coluna `categoria` e as
colunas específicas nulas quando não se aplicam.

**Alternativas.**
- Uma tabela base e uma por subclasse (`JOINED`): mais integridade no banco, mas toda leitura
  vira `JOIN` e cada categoria nova vira tabela nova.
- Uma tabela por categoria: consultar todas as aeronaves exigiria `UNION`.

**Justificativa.** É a forma mais simples para poucas categorias. Os campos obrigatórios de
cada categoria são conferidos no domínio, pelos construtores das subclasses.
