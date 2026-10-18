# ADR 0008 — Domínio em português, infraestrutura em inglês

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

O padrão de desenvolvimento que a equipe trouxe de outros projetos manda
escrever todo o código em inglês. Este trabalho, porém, é avaliado por
"qualidade e consistência do modelo de domínio" e por "coerência entre domínio,
arquitetura, documentação e implementação" (§8.4) — e o enunciado, o modelo de
domínio em UML e a apresentação são todos em português.

O vocabulário do problema é português e é específico: plano de voo, trecho,
aerovia, sobrevoo, pouso, permanência, vaga, slot, situação. Esse é o conceito
de **linguagem ubíqua** do DDD: o nome que o especialista do domínio usa deve
ser o nome que aparece no código, sem tradução no meio do caminho.

## Alternativas consideradas

1. **Tudo em inglês.** Vantagem: consistência com o padrão que a equipe já usa
   e com o ecossistema Java. Desvantagem: cria uma camada de tradução entre o
   enunciado e o código. `FlightPlan`, `Airway`, `Leg`, `Overflight` e
   `ParkingSpot` obrigam quem lê o diagrama a traduzir mentalmente para
   conferir contra a implementação — e é exatamente essa conferência que a
   avaliação faz. Não foi escolhida.

2. **Tudo em português, inclusive a infraestrutura.** Vantagem: um idioma só,
   sem regra para decorar. Desvantagem: a infraestrutura conversa com
   bibliotecas em inglês, e sairiam nomes híbridos como
   `PlanoDeVooRestControlador` ou `RepositorioJpaDeAerovias` ao lado de
   `JpaRepository` e `@RestController`. Não foi escolhida por ficar pior nos
   dois lados.

## Decisão

**O domínio é escrito em português** — `PlanoDeVoo`, `Trecho`, `Pouso`,
`Aerovia`, `Slot`, `podeOperar`, `slotsOcupados`, `distanciaMaxSemAbastecer` —
e a **infraestrutura segue as convenções em inglês** do framework, nos sufixos
e nos termos técnicos: `...Controller`, `...JpaEntity`, `...Mapper`,
`...RequestDTO`, `...ResponseDTO`, `...Config`.

Branches, mensagens de commit e títulos de PR continuam em inglês, como o
padrão da equipe já definia.

## Consequências

**Facilita.** Uma classe do diagrama de domínio é encontrada no código pelo
mesmo nome, sem tradução. Quem lê o enunciado, abre o UML e abre
`domain/model/` vê o mesmo vocabulário nos três — que é precisamente o que o
critério de coerência avalia.

**Custa.** Uma regra a mais para lembrar, e uma fronteira onde os dois idiomas
se encontram: `RepositorioAeroviasJPA implements RepositorioAerovias` ou
`AeroviaResponseDTO.de(Aerovia)`. Nomes assim são propositais, não descuido.

**Passa a ser obrigatório.**

- Nada de nome traduzido ou híbrido dentro de `domain/`: nem `FlightPlan`, nem
  `PlanoDeVooService`.
- Os sufixos continuam em inglês mesmo em classes de infraestrutura que tratam
  de conceitos do domínio, porque o sufixo é que diz em qual pasta a classe
  mora.
- O PR cobra este item explicitamente: "idioma respeitado: domínio em
  português, infraestrutura em inglês".
