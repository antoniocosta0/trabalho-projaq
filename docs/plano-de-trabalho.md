# Plano de trabalho — entrega de domingo

**Prazo de entrega: domingo, 18/10/2026, 23:00.** Apresentação na segunda, 19/10.
Isto é: **3 dias**, contando a noite de hoje (quinta, 15/10).

Este arquivo é a fonte de verdade da divisão. O WhatsApp serve para avisar que
algo mudou aqui — não para combinar o que cada um faz.

---

## 1. Quem pega o quê

| Pessoa | Frente | Esforço |
|---|---|---|
| **Vicenzo Masera** | **A** — núcleo do domínio, depois entra em B | 4h + B |
| **João Pedro Bianchi (Dejota)** | **B** — agregado e validação, com o Vicenzo | 10h a dois |
| **Pedro Redivo** | **C** — reservas, aprovação e cancelamento | 8h a dois |
| **Gustavo Both** | **C** com o Redivo, depois o `data.sql` | 8h a dois + 3h |
| **Antônio Costa** | **E** — documentação — mais os **cadastros** (UC02, 04, 06, 08) e a integração de domingo | 6h + 3h |

**Por que assim:** B e C são as duas frentes pesadas e cada uma leva uma dupla.
A é curta e o Vicenzo rola dela direto para B, sem repasse. Os cadastros são o
código mais leve do projeto — quatro `POST` copiando o padrão dos `GET` que já
existem — e combinam com quem está tocando a documentação.

---

## 2. A restrição que manda em tudo

A cadeia **não** é linear. `A` é o único gargalo de verdade:

```
A (Slot, Trecho, Pouso)
  ├──► B (agregado, validar, abandonar) ──┐
  │                                       ├──► aprovar e cancelar
  └──► C (reservas, tabelas, UC12, UC13) ─┘
                                           └──► data.sql com planos aprovados
```

As entidades de reserva, as três tabelas, os dois `UNIQUE` e as consultas de
disponibilidade dependem só do `Slot`. **C não espera B** — só o `aprovar`,
que é o último passo de C, precisa do `validar` que vem de B.

Rodando em paralelo desde o começo, sem depender de ninguém: os **cadastros** e
a maior parte da **documentação**.

**Se A atrasar, a entrega não fecha.** É a única frente com essa propriedade.

---

## 3. As frentes

### A — Núcleo do domínio · Vicenzo

Branch: `feat/route-domain-core` · **entregar sexta de manhã**

- [ ] `Slot` (value object): `inicio`, `Slot.entre(inicio, fim) : List<Slot>`
- [ ] `Trecho`: `ordem`, `altitude`, `horarioEntrada`, `horarioChegada()`,
      `slotsOcupados()`
- [ ] `Pouso` (value object): `numeroVaga`, `permanenciaMinutos`,
      `saida(chegada)`
- [ ] `SlotTest`, `TrechoTest`, `PousoTest`

**Combinar com o Antônio antes de escrever a primeira linha** — B, C e o seed
dependem destas três respostas:

1. A borda exata: entrada 15:00, saída 16:00 — **1 slot ou 2?** Decidir,
   registrar em `logs/decisoes.md` e testar.
2. `slotsOcupados()` devolve `List<Slot>` ou `Set<Slot>`?
3. `saida(chegada)` já inclui a permanência?

**Pronto quando:** `./mvnw clean test` verde e nenhum desses testes sobe o
Spring nem toca no banco.

### B — Agregado e validação · Vicenzo + Dejota

Branch: `feat/flight-plan-validation` · **depende de A** · começa sexta à tarde

- [ ] `PlanoDeVoo.adicionarTrecho`, `.removerTrecho` — só em `EM_EDICAO` (UC10, UC11)
- [ ] `PlanoDeVoo.validar() : List<String>` (UC14), com as seis verificações
- [ ] `PlanoDeVoo.abandonar()` (UC18)
- [ ] Persistência de `Trecho` e `Pouso`
- [ ] API: `POST /planos/{id}/trechos`, `DELETE /planos/{id}/trechos/{ordem}`,
      `POST /planos/{id}/validacao`, `POST /planos/{id}/abandono`

Divisão sugerida dentro da dupla: um pega continuidade do percurso, horários e
mesmo dia; o outro pega aeronave, autonomia e disponibilidade.

**Pronto quando:** validar devolve a lista de problemas, **não muda estado** e
**não cria reserva nenhuma**.

### C — Reservas, aprovação e cancelamento · Redivo + Both

Branch: `feat/reservations-and-approval` · **depende de A** · começa sexta à tarde

- [ ] `ReservaAerovia` e `ReservaVaga` no domínio, com portas de saída
- [ ] `schema.sql`: tabelas `trecho`, `reserva_aerovia`, `reserva_vaga`, com
      `UNIQUE (aerovia_id, altitude, slot_inicio)` e
      `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)`
- [ ] UC12 e UC13: consultar altitudes e vagas livres por período
- [ ] `AprovarPlanoUC` (UC15) em **uma** `@Transactional` — **só depois de B**
- [ ] `CancelarPlanoUC` (UC19): só `APROVADO` com partida futura
- [ ] **Teste que o enunciado cobra por escrito:** aprovação malsucedida não
      deixa reserva parcial

**Pronto quando:** aprovar a mesma altitude no mesmo slot duas vezes falha na
segunda, e a primeira continua íntegra.

### D1 — Cadastros · Antônio

Branch: `feat/registration-endpoints` · **não depende de nada, começa já**

- [ ] `POST /aeroportos` (UC02)
- [ ] `POST /aerovias` (UC04) — sem `PUT` e sem `DELETE`: aerovia é imutável
- [ ] `POST /pilotos` (UC06)
- [ ] `POST /aeronaves` (UC08) — Factory por categoria, para o controller não
      conhecer as subclasses

É o código mais leve do projeto: cada endpoint é o espelho de um `GET` que já
existe. `MalhaAereaController` + `ConsultarAeroviasUC` + `RepositorioAeroviasJPA`
é o modelo a copiar.

**Pronto quando:** dá para cadastrar um aeroporto novo e usar num plano, sem
recompilar — é o teste de extensibilidade que o professor vai fazer.

### D2 — Carga inicial · Both

Branch: `db/initial-seed` · **depende de C** · sábado

- [ ] `data.sql`: planos **aprovados** com suas reservas, gerando ocupação em
      **5 ou mais das 8 aerovias** na data de referência
- [ ] As ocupações são consequência dos planos aprovados — nada de linha solta
      na tabela de reservas
- [ ] Deixar folga: não pode ficar tudo indisponível o dia inteiro, senão não
      dá para demonstrar aprovação bem-sucedida
- [ ] Rodar o roteiro do `CLAUDE.md` §14.4: apaga o banco, sobe, confere as
      contagens, cria/valida/aprova um plano, **reinicia** e confirma que
      continua lá

Fica com quem fez C porque é quem sabe exatamente como a reserva é gravada.

### E — Documentação e apresentação · Antônio

Branch: `docs/adrs-and-readme` · **a maior parte começa já**

Pode escrever agora — os rascunhos das 8 decisões já estão em `logs/decisoes.md`:

- [ ] ADR 0001 — estilo arquitetural (hexagonal × camadas)
- [ ] ADR 0002 — entidade de domínio separada da de persistência
- [ ] ADR 0003 — banco escolhido, e por que não em memória
- [ ] ADR 0005 — cálculo de slots como value object
- [ ] ADR 0006 — categoria nova de aeronave sem alterar a validação
- [ ] ADR 0007 — herança da hierarquia de aeronaves no banco
- [ ] ADR 0008 — idioma do código
- [ ] springdoc descomentado no `pom.xml`, com a versão compatível conferida, e
      Swagger UI abrindo

Depende das outras frentes:

- [ ] ADR 0004 — atomicidade da aprovação · **depois de C**
- [ ] `README.md`, os sete `[PREENCHER]` · a data de referência depende de D2
- [ ] `docs/api/requests.http` com as **17 demonstrações** do `CLAUDE.md` §16.3
      · cresce junto com os endpoints, fecha no domingo
- [ ] Ensaiar o roteiro na ordem, cronometrado

**Pronto quando:** alguém de fora clona o repositório, segue o README e faz a
primeira requisição sem perguntar nada.

---

## 4. Calendário

| Quando | Vicenzo | Dejota | Redivo | Both | Antônio |
|---|---|---|---|---|---|
| **Qui 15, noite** | A | — | — | — | assinaturas de A com o Vicenzo, depois ADRs |
| **Sex 16, manhã** | **fecha A** | — | prepara C | prepara C | cadastros |
| **Sex 16, tarde** | B | B | C | C | ADRs + springdoc |
| **Sáb 17** | B | **fecha B** | aprovar e cancelar | `data.sql` | README + `requests.http` |
| **Dom 18, até 18:00** | apoio | apoio | **fecha C** | roteiro §14.4 | ADR 0004, ensaio |
| **Dom 18, 18:00–23:00** | todos: integração, último PR, conferência final, entrega | | | | |

Deixar as cinco horas finais de domingo para integração não é folga: é onde
aparecem os conflitos de merge e os bugs de borda.

---

## 5. Regras de trabalho

- **Uma branch por frente**, no padrão `<tipo>/<descricao>` em inglês
  (`docs/padroes-desenvolvimento.md`).
- **Commit a cada caso de uso que fecha.** Commit único e gigante no domingo
  conta uma história ruim no histórico, mesmo com o código certo.
- **PR contra `main`** com o template preenchido com verdade — inclusive o que
  NÃO foi testado.
- **`./mvnw clean test` verde antes de cada commit**, rodado depois da última
  alteração.
- **`domain/` não importa Spring nem JPA.** A conferência é literal:
  `grep -rn "org.springframework\|jakarta.persistence\|infra\." backend/src/main/java/com/pucrs/planosdevoo/domain/`
  tem que devolver nada.
- **Nada de `if (x instanceof AeronaveCarga)`.** Regra por categoria é
  polimorfismo — é o ponto de extensão que o professor vai testar.
- **Mexeu no domínio, atualiza o diagrama no mesmo PR.**
- **Fim de sessão: entrada em `logs/log.md`**, e em `logs/decisoes.md` se houve
  decisão. Os logs são locais (cada um tem o seu) e são a matéria-prima da
  apresentação.

---

## 6. Decisão pendente que bloqueia a frente D

**Data de referência: `[A CONFIRMAR]`**

Recomendação: **19/10/2026**, o dia da apresentação. Com planos partindo de
manhã e de noite no `data.sql`, uma data só resolve dois requisitos de uma vez
— os da manhã já partiram (demonstram que plano com partida passada **não** é
cancelável) e os da noite ainda são futuros (demonstram o cancelamento com
liberação de reservas).

Qualquer outra data exige uma segunda data no passado só para a demonstração do
cancelamento recusado. Quem pegar a frente D precisa disso definido na sexta,
no mais tardar.

---

## 7. O que já está pronto

- Estrutura hexagonal em `backend/`, com a regra de dependência respeitada
- 7 dos 19 casos de uso: UC01, UC03, UC05, UC07, UC09, UC16, UC17
- H2 em arquivo, `schema.sql` e `data.sql` com malha, pilotos e aeronaves
- Modelo de domínio e os três C4, em `docs/` (PR #3)
- As 8 decisões arquiteturais registradas em `logs/decisoes.md`, prontas para
  virar ADR
