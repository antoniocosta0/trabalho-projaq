# Plano de trabalho — entrega de domingo

**Prazo de entrega: domingo, 18/10/2026, 23:55**, por envio no Moodle
(`Trabalho final (parte 1)`, grupo `GrupoTF_P1 6`). Apresentação na segunda,
19/10.
Isto é: **3 dias**, contando a noite de hoje (quinta, 15/10).

Este arquivo é a fonte de verdade da divisão. O WhatsApp serve para avisar que
algo mudou aqui — não para combinar o que cada um faz.

---

## 1. Quem pega o quê

| Pessoa | Frente |
|---|---|
| **Vicenzo Masera** | **A** — núcleo do domínio · **concluída em 16/10**. Depois: revisa os PRs e ajuda quem travar |
| **Gustavo Both** | **B** — agregado, percurso, validação e abandono |
| **Pedro Redivo** | **C** — reservas, disponibilidade, aprovação e cancelamento |
| **João Pedro Bianchi (Dejota)** | **D** — cadastros e carga inicial (`data.sql`) |
| **Antônio Costa** | **E** — documentação e apresentação, mais a integração de domingo e o **envio no Moodle** |

**Por que assim:** uma pessoa por frente, cada uma com dono claro, para não haver
duas pessoas escrevendo o mesmo código e conflito na hora de juntar. Quem
precisar de algo de outra frente combina direto com o dono dela.

---

## 2. A restrição que manda em tudo

A cadeia **não** é linear. `A` é o único gargalo de verdade:

```
A (Slot, Trecho, Pouso)
  ├──► B (agregado, validar, abandonar) ──┐
  │                                       ├──► aprovar e cancelar
  └──► C (reservas, tabelas, UC07, UC08) ─┘
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

### A — Núcleo do domínio · Vicenzo · ✅ concluída

Branch: `feat/route-domain-core` · **entregue em 16/10**

- [x] `Slot` (value object): `inicio`, `Slot.entre(inicio, fim) : List<Slot>`
- [x] `Trecho`: `ordem`, `altitude`, `horarioEntrada`, `horarioChegada()`,
      `slotsOcupados()`
- [x] `Pouso` (value object): `numeroVaga`, `permanenciaMinutos`,
      `saida(chegada)`
- [x] `SlotTest`, `TrechoTest`, `PousoTest`

**Combinar com o Antônio antes de escrever a primeira linha** — B, C e o seed
dependem destas três respostas:

1. A borda exata: entrada 15:00, saída 16:00 — **1 slot ou 2?** Decidir,
   registrar em `logs/decisoes.md` e testar.
2. `slotsOcupados()` devolve `List<Slot>` ou `Set<Slot>`?
3. `saida(chegada)` já inclui a permanência?

**Pronto quando:** `./mvnw clean test` verde e nenhum desses testes sobe o
Spring nem toca no banco.

### B — Agregado e validação · Gustavo · ✅ concluída

Branch: `feat/flight-plan-validation` · **concluída em 17/10** (PR #8)

- [x] `PlanoDeVoo.adicionarTrecho`, `.alterarTrecho`, `.removerTrecho` — só em
      `EM_EDICAO`, com renumeração dos seguintes (UC06)
- [x] `PlanoDeVoo.validar() : List<String>` (UC09), com as seis verificações
- [x] `PlanoDeVoo.abandonar()` (UC11)
- [x] Persistência de `Trecho` e `Pouso`
- [x] API: `POST /planos/{id}/trechos`, `PUT /planos/{id}/trechos/{ordem}`,
      `DELETE /planos/{id}/trechos/{ordem}`, `POST /planos/{id}/validacao`,
      `POST /planos/{id}/abandono`

**Pronto quando:** validar devolve a lista de problemas, **não muda estado** e
**não cria reserva nenhuma**.

### C — Reservas, aprovação e cancelamento · Redivo · ✅ concluída

Branch: `feat/reservations-and-approval` · **concluída em 18/10** (PR #10)

- [x] `ReservaAerovia` e `ReservaVaga` no domínio, com portas de saída
- [x] `schema.sql`: tabelas `reserva_aerovia` e `reserva_vaga`, com
      `UNIQUE (aerovia_id, altitude, slot_inicio)` e
      `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)` (a `trecho` veio no PR #8)
- [x] UC07 e UC08: consultar altitudes e vagas livres por período
- [x] `AprovarPlanoUC` (UC10) numa transação só — pela porta `Transacao`, e não
      por `@Transactional`, que poria Spring dentro de `domain/` (ADR 0004)
- [x] `CancelarPlanoUC` (UC12): só `APROVADO` com partida futura
- [x] **Teste que o enunciado cobra por escrito:** aprovação malsucedida não
      deixa reserva parcial (`AprovacaoAtomicaTest`)

**Pronto quando:** aprovar a mesma altitude no mesmo slot duas vezes falha na
segunda, e a primeira continua íntegra.

### D1 — Cadastros · Dejota · ✅ concluída

**Não são casos de uso** (orientação do professor: a malha, os pilotos e as
aeronaves vêm da carga inicial). Ficam na API porque o enunciado (§2 e §8.1)
pede que dê para cadastrar novos aeroportos e aerovias. Prioridade baixa: se o
tempo apertar, `POST /aeroportos` e `POST /aerovias` primeiro.

Branch: `feat/registration-endpoints` · **concluída em 18/10** (PR #17)

- [x] `POST /aeroportos`
- [x] `POST /aerovias` — sem `PUT` e sem `DELETE`: aerovia é imutável, e id
      repetido é recusado com 409
- [x] `POST /pilotos` — id gerado pelo banco a partir de 101
- [x] `POST /aeronaves` — `FabricaDeAeronaves` no domínio, único ponto que
      conhece as subclasses

É o código mais leve do projeto: cada endpoint é o espelho de um `GET` que já
existe. `MalhaAereaController` + `ConsultarAeroviasUC` + `RepositorioAeroviasJPA`
é o modelo a copiar.

**Pronto quando:** dá para cadastrar um aeroporto novo e usar num plano, sem
recompilar — é o teste de extensibilidade que o professor vai fazer.

### D2 — Carga inicial · Dejota · ✅ concluída

Branch: `db/initial-seed` · **concluída em 18/10** (PR #18)

- [x] `data.sql`: quatro planos **aprovados** com suas reservas, ocupando
      **6 das 8 aerovias** na data de referência
- [x] As ocupações são consequência dos planos aprovados — conferido a cada
      build pelo `SeedConsistenciaTest`, que compara o seed com o que o domínio
      geraria
- [x] Folga preservada: R1 e R3 ficam livres o dia inteiro
- [x] Rodar o roteiro do `CLAUDE.md` §14.4 — rodado em 18/10 com o banco
      apagado, antes e depois da carga inicial entrar: criou, validou, aprovou,
      reiniciou a aplicação, confirmou que planos e reservas sobreviveram, e
      conferiu as **6 de 8** aerovias ocupadas na data de referência

Depende de C: combinar com o Redivo como cada reserva é gravada no banco.

### E — Documentação e apresentação · Antônio

Branch: `docs/adrs-and-readme` · **a maior parte começa já**

Pode escrever agora — os rascunhos das 8 decisões já estão em `logs/decisoes.md`:

- [x] ADR 0001 — estilo arquitetural (hexagonal × camadas)
- [x] ADR 0002 — entidade de domínio separada da de persistência
- [x] ADR 0003 — banco escolhido, e por que não em memória
- [x] ADR 0005 — cálculo de slots como value object
- [x] ADR 0006 — categoria nova de aeronave sem alterar a validação
- [x] ADR 0007 — herança da hierarquia de aeronaves no banco
- [x] ADR 0008 — idioma do código
- [x] ADR 0009 — cadastros fora da lista de casos de uso (orientação do
      professor), mas mantidos na API por causa do enunciado §2 e §8.1
- [x] springdoc descomentado no `pom.xml`, com a versão compatível conferida, e
      Swagger UI abrindo

Depende das outras frentes:

- [x] ADR 0004 — atomicidade da aprovação (PR #11)
- [x] `README.md`, os sete `[PREENCHER]` · data de referência declarada: 19/10/2026
- [x] `docs/api/requests.http` com as **17 demonstrações** do `CLAUDE.md` §16.3
      — fechado em 18/10, com cada bloco executado contra a aplicação
- [ ] Ensaiar o roteiro na ordem, cronometrado

**Pronto quando:** alguém de fora clona o repositório, segue o README e faz a
primeira requisição sem perguntar nada.

---

## 4. Calendário

| Quando | Vicenzo | Gustavo | Redivo | Dejota | Antônio |
|---|---|---|---|---|---|
| **Sex 16** | **fecha A** | — | — | — | ADRs |
| **Sáb 17** | revisão de PRs, apoio | B: percurso e trechos | C: reservas, tabelas, UC07 e UC08 | D1: cadastros | ADRs + springdoc |
| **Dom 18, até 18:00** | apoio | **fecha B**: validar e abandonar | aprovar e cancelar (precisa de B), **fecha C** | D2: `data.sql` e roteiro §14.4 | README, `requests.http`, ensaio |
| **Dom 18, 18:00–23:55** | todos: integração, último PR, conferência final; Antônio envia no Moodle | | | | |

Deixar as cinco horas finais de domingo para integração não é folga: é onde
aparecem os conflitos de merge e os bugs de borda.

---

## 4.1. O envio no Moodle — responsável: Antônio

Até agora **nada foi enviado**. A tarefa está aberta em
`Trabalho final (parte 1)`, grupo `GrupoTF_P1 6`, com vencimento em
**domingo, 18/10/2026, 23:55**.

O enunciado (§8.5) diz que a entrega é **por repositório Git**, então o envio é
o link do repositório — não um `.zip` de código. Confirmar no Moodle o formato
aceito (link ou arquivo) **antes de domingo**, não às 23:40.

- [ ] Conferir o formato de envio aceito pela tarefa
- [ ] Enviar até **domingo 22:00**, com margem — e não às 23:55
- [ ] Confirmar que o status saiu de "Nada foi enviado para esta tarefa"

Esta é a única tarefa da lista que, se for esquecida, anula todo o resto.

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

**Data de referência: `19/10/2026` — confirmada em 18/10 e declarada no `README.md`.**

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

- Estrutura hexagonal em `backend/`, com a regra de dependência respeitada — e
  agora conferida automaticamente a cada push e PR, pelo CI (PR #12)
- **Os 14 casos de uso**: UC01 a UC14, todos com endpoint. UC01–UC05, UC13 e
  UC14 no PR #2; UC06, UC09 e UC11 no PR #8; UC07, UC08, UC10 e UC12 no PR #10
- **Todas as cinco frentes concluídas**, A a E
- 127 testes automatizados, verdes, rodando também no CI com JDK 25
- Carga inicial com planos aprovados, ocupando 6 das 8 aerovias na data de
  referência, e os quatro cadastros atendendo o §8.1
- As nove decisões arquiteturais escritas como ADR, de 0001 a 0009
- **Frente A concluída** (Vicenzo, 16/10): `Slot`, `Trecho` e `Pouso`, com testes.
  Borda decidida: intervalo `[início, fim)`, então 15:00 → 16:00 ocupa **1 slot**
- H2 em arquivo, `schema.sql` e `data.sql` com malha, pilotos e aeronaves
- Modelo de domínio e os três C4, em `docs/` (PR #3)
- As 8 decisões arquiteturais registradas em `logs/decisoes.md`, prontas para
  virar ADR
