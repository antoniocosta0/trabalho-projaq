# ADR 0001 — Arquitetura hexagonal

**Decisão.** O backend segue ports and adapters. `domain` tem o modelo, os serviços e as portas;
`infra` tem os adaptadores REST e de persistência e a configuração. As dependências apontam
sempre para o domínio. Nomes: domínio em português, papel técnico em inglês como sufixo
(`PlanoDeVooService`, `PlanoDeVooRepository`, `PlanoDeVooController`).

**Alternativas.**
- Camadas tradicionais (controller → service → repository JPA): menos código, mas as regras
  ficariam presas ao Spring e ao JPA.
- Um caso de uso por classe: 18 classes pequenas, difíceis de acompanhar. Agrupamos em quatro
  serviços (`MalhaAerea`, `Frota`, `Disponibilidade`, `PlanoDeVoo`), um por assunto.

**Justificativa.** O enunciado avalia separação de responsabilidades, facilidade de testar e
evolução. Com o domínio sem framework, as regras são testadas sem Spring e a parte 2 pode
trocar adaptadores sem tocar nelas. Os serviços não têm anotação do Spring: viram beans em
`ServiceConfig`. O CI confere que `domain` não importa Spring, JPA nem `infra`.
