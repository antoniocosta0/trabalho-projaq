# Decisões arquiteturais (ADR)

Um arquivo por decisão, no formato de `0000-template.md`. O rascunho de cada
uma nasce nas anotações de trabalho da equipe; aqui fica a versão entregue.

O enunciado exige, para cada decisão relevante: **a decisão adotada, as
principais alternativas consideradas e a justificativa da escolha** (seção 7.4).

## Decisões que este trabalho precisa ter registradas

| ADR | Decisão | Situação |
|---|---|---|
| 0001 | Estilo arquitetural (hexagonal × camadas × outro) | [escrita](0001-estilo-arquitetural-hexagonal.md) |
| 0002 | Entidade de domínio separada da entidade de persistência | [escrita](0002-entidade-de-dominio-separada-da-de-persistencia.md) |
| 0003 | Banco de dados escolhido (e por que não em memória) | [escrita](0003-banco-h2-em-arquivo.md) |
| 0004 | Atomicidade da aprovação (transação, constraint, lock) | [escrita](0004-atomicidade-da-aprovacao.md) |
| 0005 | Modelagem do cálculo de slots (value object `Slot`) | [escrita](0005-slot-como-value-object.md) |
| 0006 | Extensão para novas categorias de aeronave sem alterar a validação | [escrita](0006-regra-de-operacao-polimorfica.md) |
| 0007 | Estratégia de herança da hierarquia de aeronaves no banco | [escrita](0007-heranca-de-aeronave-em-tabela-unica.md) |
| 0008 | Idioma do código: domínio em português, infraestrutura em inglês | [escrita](0008-idioma-do-codigo.md) |
| 0009 | Cadastros fora da lista de casos de uso | [escrita](0009-cadastros-fora-da-lista-de-casos-de-uso.md) |
| 0010 | Interpretações do enunciado (bordas de slot, janela da carga, arredondamento, relógio) | [escrita](0010-interpretacoes-do-enunciado.md) |
