# Decisões e conversas — `trabalho-1`

O que foi **decidido** e por quê. Append-only. Este arquivo é o rascunho dos
ADRs de `docs/arquitetura/decisoes/`: toda decisão fechada aqui vira um ADR lá.

O campo mais valioso é **"Decisões" com a alternativa descartada** — é ele que
responde o professor quando pergunta "por que não fez do outro jeito?".

Formato de cada entrada (ver `CLAUDE.md`, seção 9.3):

```markdown
## AAAA-MM-DD — título curto

**Assunto.** Do que se tratou, em 1 ou 2 linhas.

**Decisões.**
- O que ficou decidido e por quê. Alternativa descartada e o motivo.

**Em aberto.**
- Pergunta sem resposta, e do que ela depende.

**Achados técnicos.**
- Fato verificado, com a referência (arquivo e seção).

**Retomar por aqui.**
- O próximo passo, escrito para ser lido dias depois sem contexto.
```

---

## 2026-09-30 — Padrões de desenvolvimento e idioma do código

**Assunto.** Definição dos padrões de Git, nomenclatura e template de PR do
repositório, a partir dos padrões já usados no projeto de AGES II.

**Decisões.**
- **Branch sem ID de task.** O projeto de AGES II permite o ID quando existe;
  aqui não há ferramenta de tasks, então o rastro é o caso de uso (UC01–UC19) e
  a entrega do cronograma, citados no corpo do PR. Alternativa descartada:
  inventar uma numeração própria de tasks — ela não teria nada do outro lado.
- **Branch de integração é `main`**, não `develop`. Alternativa descartada:
  `develop` + `main`, como na AGES. Para um trabalho de três semanas, o fluxo
  de duas branches longas só adiciona cerimônia sem nenhum ambiente de
  homologação para justificá-la.
- **Idioma: domínio em português, infraestrutura em inglês.** É uma divergência
  consciente do padrão de AGES II, que manda tudo em inglês. Motivo: a
  linguagem ubíqua do DDD (`P5`, `P6`) e a avaliação de "coerência entre
  domínio, arquitetura, documentação e implementação" — traduzir `PlanoDeVoo`
  para `FlightPlan` quebra a correspondência direta entre enunciado, diagrama e
  código. Alternativa descartada: tudo em inglês, com um glossário de tradução;
  seria mais um artefato para manter sincronizado e mais uma fonte de
  divergência. Vira ADR 0008.
- **Tipos de mudança adaptados:** saíram `UI` e `Deploy` (não há frontend nem
  publicação); entraram `Database` e `Test`.

**Em aberto.**
- Estilo arquitetural definitivo: a hexagonal do `P7a` é a candidata forte, mas
  a decisão só fecha com o ADR 0001 escrito e as alternativas comparadas.
- Entidade de domínio separada da entidade de persistência, ou a mesma classe?
  Depende da escolha entre JPA e JDBC. Vira ADR 0002.
- Banco: H2 em arquivo ou PostgreSQL em container? Em memória está descartado —
  o enunciado exige que o estado sobreviva à reinicialização (seção 8.2).

**Achados técnicos.**
- A estrutura de pastas da hexagonal adotada é a do material da cadeira, não
  uma invenção: `Domain (Model, Ports In/Out)` e `Infra (Adapters In/Out,
  Config)`, descrita em `P7a_EstudoDeCasoArquiteturaHexagonal.pdf`, junto com os
  cinco passos do fluxo de requisição.
- O exemplo `ex1-biblioteca-v3-rotasComParametros` usa Spring Boot `4.1.1` e
  Java `25`. São essas as versões de partida.
- O enunciado proíbe alterar ou excluir aerovia após o cadastro (seção 2). Isso
  é restrição de modelagem, não só de API: nada de setter público em `Aerovia`.

**Retomar por aqui.**
- Criar o projeto Spring Boot em `backend/` com a árvore de pacotes da seção 8
  do `CLAUDE.md`, e commitar a estrutura vazia como a entrega de 07/10.
- Em paralelo, fechar o modelo de domínio em UML para 05/10, partindo do
  desenho já existente (`CLAUDE.md` seção 6).
