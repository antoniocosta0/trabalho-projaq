# ADR 0007 — Hierarquia de aeronaves em tabela única

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

A ADR 0006 estabeleceu uma hierarquia de aeronaves no domínio: uma classe
abstrata e três subclasses, cada uma com atributos próprios — empresa de
manutenção (particular), companhia aérea e capacidade de passageiros
(passageiros), companhia aérea e capacidade em toneladas (carga).

Essa hierarquia precisa ser gravada em um banco relacional, que não tem
herança. As estratégias clássicas são tabela única, tabela por subclasse
(joined) e tabela por classe concreta.

Há uma particularidade importante: pela ADR 0002, a entidade de domínio e a
entidade de persistência são classes **separadas**. A hierarquia existe só do
lado do domínio; do lado da persistência não há obrigação de reproduzi-la, e
nem de usar os mecanismos de herança do JPA.

## Alternativas consideradas

1. **`JOINED` — uma tabela base e uma por subclasse.** Vantagem: cada coluna
   fica na tabela onde ela faz sentido, com `NOT NULL` onde é obrigatória —
   integridade de verdade no nível do banco. Desvantagem: toda leitura de
   aeronave vira `JOIN`, e categoria nova vira tabela nova, ou seja, alteração
   de schema. Não foi escolhida: para quatro aeronaves na carga inicial, a
   integridade extra não compensa o custo de schema e de consulta.

2. **`TABLE_PER_CLASS` — uma tabela independente por categoria.** Vantagem:
   nenhuma coluna nula. Desvantagem: consultar "todas as aeronaves" exige
   `UNION` entre as tabelas, e a unicidade do prefixo — que vale para a frota
   inteira — deixa de ser garantível por uma `PRIMARY KEY`. Não foi escolhida
   por esse segundo motivo, que é um requisito real (prefixo é único).

3. **Reproduzir a hierarquia do domínio em entidades JPA com `@Inheritance`.**
   Vantagem: simetria entre os dois lados. Desvantagem: triplica as classes de
   persistência e o mapeamento, sem nenhum ganho — o adaptador de saída não
   precisa de polimorfismo, ele precisa de linhas. Não foi escolhida.

## Decisão

**Tabela única.** Uma tabela `aeronave`, com `prefixo` como chave primária, as
colunas comuns (`categoria`, `velocidade_cruzeiro_kmh`, `autonomia_km`) e as
colunas específicas de cada categoria, anuláveis: `empresa_manutencao`,
`companhia_aerea`, `capacidade_passageiros`, `capacidade_toneladas`.

Do lado Java há **uma única** entidade de persistência plana,
`AeronaveJpaEntity`, sem anotação de herança. A coluna `categoria` é o
discriminador, e a `FabricaDeAeronaves` decide qual subclasse de domínio
instanciar — o `FrotaMapper` apenas a chama.

## Consequências

**Facilita.** Consultar a frota inteira é um `SELECT` simples; `prefixo` é
chave primária de verdade, garantindo a unicidade exigida; e categoria nova
**não altera o schema** — entra uma subclasse no domínio, um `case` no mapeador
e, se tiver atributo próprio, uma coluna anulável a mais.

**Custa.** Colunas nulas: uma aeronave particular tem `companhia_aerea`,
`capacidade_passageiros` e `capacidade_toneladas` em branco. O banco não
consegue impor "aeronave de carga tem que ter capacidade em toneladas" — essa
validação passa a ser responsabilidade do domínio: o construtor de cada
subclasse recusa o que não faz sentido para a categoria, e a
`FabricaDeAeronaves` recusa antes disso o campo ausente.
É a troca consciente desta decisão: menos integridade declarativa, mais
flexibilidade de extensão.

**Passa a ser obrigatório.**

- A obrigatoriedade dos atributos de cada categoria é validada no construtor da
  subclasse correspondente, já que o banco não pode fazê-lo.
- O valor de `categoria` gravado é a `String` devolvida por
  `Aeronave.getCategoria()`, e precisa bater com o `case` da `FabricaDeAeronaves`.
  Nunca gravar ordinal de enum.
- Atributo novo de categoria entra como coluna **anulável**, para não quebrar
  as linhas das outras categorias.
