# ADR 0002 — Entidade de domínio separada da entidade JPA

**Decisão.** Cada entidade de domínio tem uma entidade JPA correspondente em
`infra/adapters/out/persistence/entity`, com um mapper entre as duas.

**Alternativas.**
- Anotar as classes de domínio com `@Entity`: menos código, mas o domínio passa a depender
  do JPA e os construtores passam a ser ditados pelo Hibernate.

**Justificativa.** Mantém o domínio puro (ADR 0001) e permite que o modelo e as tabelas
evoluam separadamente. O custo é a duplicação de estrutura nos mappers.
