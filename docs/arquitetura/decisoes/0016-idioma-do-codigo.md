# ADR 0016 — Domínio em português, infraestrutura em inglês

**Data:** 2026-10-18 (parte 1), estendida aos novos serviços em 2026-11-11
**Situação:** aceita

## Contexto

Herdada da parte 1 (onde ficou registrada só em `docs/padroes-desenvolvimento.md`, sem ADR
própria). Com a extração de `cadastro` e `registro` na parte 2, a decisão passa a valer para mais
de um módulo e merece registro formal.

O vocabulário do problema é português e específico: plano de voo, trecho, aerovia, sobrevoo,
pouso, vaga, slot, aprovação, registro. É a linguagem ubíqua do domínio e do enunciado.

## Alternativas consideradas

1. **Tudo em inglês.** Consistente com o ecossistema Java, mas cria uma camada de tradução entre
   o enunciado, os diagramas e o código — exatamente o que a avaliação confere. Não foi escolhida.
2. **Tudo em português, inclusive a infraestrutura.** Um idioma só, mas gera nomes híbridos ao
   lado das convenções do framework (`@RestController`, `JpaRepository`, `RabbitListener`). Não
   foi escolhida.

## Decisão

O domínio de cada serviço de negócio (`backend`, `cadastro`, `registro`) é escrito em português —
`PlanoDeVoo`, `Aerovia`, `MalhaAereaService`, `RegistroAprovacaoService`. A infraestrutura segue as
convenções em inglês do framework nos sufixos e termos técnicos — `Controller`, `Entity`, `Mapper`,
`Repository`, `Config`, `Publisher`, `Listener` — mesmo quando o nome da classe cita um conceito do
domínio (`MalhaAereaHttpAdapter`, `AprovacaoRabbitPublisher`). Branch, commit e identificadores do
Gateway e do Eureka continuam em inglês.

## Consequências

Mantém a mesma linguagem do enunciado, do modelo de domínio e dos diagramas em todos os serviços,
não só no `backend` original — relevante porque `cadastro` e `registro` são lidos e avaliados do
mesmo jeito. Continua obrigatório: nenhum nome traduzido ou híbrido dentro de `domain/`; os sufixos
de infraestrutura (seção 2.1 de `docs/padroes-desenvolvimento.md`) continuam em inglês mesmo em
classes que tratam de conceitos do domínio.
