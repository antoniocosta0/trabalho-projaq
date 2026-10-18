# Decisões arquiteturais (ADR)

Um arquivo por decisão, no formato de `0000-template.md`. O rascunho de cada
uma nasce em `logs/decisoes.md`; aqui fica a versão limpa, que é entregue.

O enunciado exige, para cada decisão relevante: **a decisão adotada, as
principais alternativas consideradas e a justificativa da escolha** (seção 7.4).

## Decisões que este trabalho precisa ter registradas

| ADR | Decisão | Situação |
|---|---|---|
| 0001 | Estilo arquitetural (hexagonal × camadas × outro) | [escrita](0001-estilo-arquitetural-hexagonal.md) |
| 0002 | Entidade de domínio separada da entidade de persistência | [escrita](0002-entidade-de-dominio-separada-da-de-persistencia.md) |
| 0003 | Banco de dados escolhido (e por que não em memória) | [escrita](0003-banco-h2-em-arquivo.md) |
| 0004 | Atomicidade da aprovação (transação, constraint, lock) | a escrever |
| 0005 | Modelagem do cálculo de slots (value object `Slot`) | a escrever |
| 0006 | Extensão para novas categorias de aeronave sem alterar a validação | a escrever |
| 0007 | Estratégia de herança da hierarquia de aeronaves no banco | a escrever |
| 0008 | Idioma do código: domínio em português, infraestrutura em inglês | a escrever |
