# ADR 0003 — H2 em arquivo

**Decisão.** Banco H2 em modo arquivo (`./data/planosdevoo`), com `schema.sql` e `data.sql`
versionados e executados na inicialização.

**Alternativas.**
- H2 em memória: não sobrevive à reinicialização, o que o §7.3 proíbe.
- PostgreSQL em Docker: mais próximo de produção, mas exige instalar e subir um serviço
  à parte para avaliar o trabalho.

**Justificativa.** Atende à persistência exigida sem dependência externa: basta Java para
rodar. O schema não é gerado pelo Hibernate para ser reproduzível.
