# Plano de trabalho — entrega de domingo

**Prazo de entrega: domingo, 18/10/2026, 23:00.** Apresentação na segunda, 19/10.
Isto é: **3 dias**, contando a noite de hoje (quinta, 15/10).

Este arquivo é a fonte de verdade da divisão. O WhatsApp serve para avisar que
algo mudou aqui — não para combinar o que cada um faz.

---

## 1. Quem pega o quê

| Frente | O que é | Responsável |
|---|---|---|
| **A** | Núcleo do domínio: `Slot`, `Trecho`, `Pouso` + testes | `[A DEFINIR]` |
| **B** | `PlanoDeVoo` como agregado: percurso, validação, abandono | `[A DEFINIR]` |
| **C** | Reservas, aprovação e cancelamento | `[A DEFINIR]` |
| **D** | Cadastros e carga inicial | `[A DEFINIR]` |
| **E** | ADRs, README, OpenAPI e roteiro da apresentação | `[A DEFINIR]` |

Equipe: Antônio Costa, João Pedro Bianchi (Dejota), Vicenzo Masera,
Pedro Redivo (Redivo), Gustavo Both (Both).

**Sugestão de formato, se o grupo preferir dobrar o caminho crítico:** duas
pessoas em **A+B** (é a parte mais difícil e a que bloqueia todas as outras) e
as três restantes em C, D e E. Nesse caso a frente E ganha ajuda no sábado,
quando A já estiver fechada.

---

## 2. A restrição que manda em tudo

O trabalho é **sequencial**, não paralelo:

```
A (Slot, Trecho, Pouso)
   └─► B (agregado, validar)
          └─► C (reservar, aprovar, cancelar)
                 └─► D (carga inicial com planos aprovados)
```

Quem pegar B não consegue começar de verdade antes de A entregar. Quem pegar D
não consegue escrever o `data.sql` final antes de C existir. **Por isso A é a
primeira coisa a ser feita e não pode atrasar** — se A escorregar para sábado,
a entrega não fecha.

A frente **E é a única 100% paralela**: começa agora e não depende de ninguém.

---

## 3. As frentes

### Frente A — Núcleo do domínio (bloqueia todo o resto)

Branch: `feat/route-domain-core`

- [ ] `Slot` (value object): `inicio`, `Slot.entre(inicio, fim) : List<Slot>`
- [ ] `SlotTest`: 15:52→17:07 = **3 slots**. Decidir, documentar e testar a
      borda exata 15:00→16:00 (1 slot ou 2?) — escolhe, registra em
      `logs/decisoes.md` e testa
- [ ] `Trecho`: `ordem`, `altitude`, `horarioEntrada`, `horarioChegada()`,
      `slotsOcupados()`
- [ ] `Pouso` (value object): `numeroVaga`, `permanenciaMinutos`,
      `saida(chegada)`
- [ ] `TrechoTest`: duração e chegada; `PousoTest`: saída após permanência

**Pronto quando:** `./mvnw clean test` verde, e nenhum desses testes sobe o
Spring nem toca no banco. Se um teste de regra precisa subir a aplicação, a
arquitetura não está isolando nada.

### Frente B — Agregado e validação (UC10, UC11, UC14, UC18)

Branch: `feat/flight-plan-validation` · **depende de A**

- [ ] `PlanoDeVoo.adicionarTrecho`, `.removerTrecho` — só em `EM_EDICAO`
- [ ] `PlanoDeVoo.validar() : List<String>` com as seis verificações, nesta
      ordem: continuidade do percurso e pouso obrigatório no destino;
      horários batendo com a chegada calculada; mesmo dia; aeronave
      (`podeOperar`); autonomia (85%, sobrevoo acumula e pouso zera);
      disponibilidade
- [ ] `PlanoDeVoo.abandonar()` (UC18)
- [ ] Testes de cada tipo de falha, um por um
- [ ] API: `POST /planos/{id}/trechos`, `DELETE /planos/{id}/trechos/{ordem}`,
      `POST /planos/{id}/validacao`, `POST /planos/{id}/abandono`
- [ ] Persistência de `Trecho` e `Pouso`

**Pronto quando:** validar devolve a lista de problemas, **não muda estado** e
**não cria reserva nenhuma**.

### Frente C — Reservas, aprovação e cancelamento (UC12, UC13, UC15, UC19)

Branch: `feat/reservations-and-approval` · **depende de A e B**

- [ ] `ReservaAerovia` e `ReservaVaga` no domínio, com portas de saída
- [ ] `schema.sql`: tabelas `trecho`, `reserva_aerovia`, `reserva_vaga`, com
      `UNIQUE (aerovia_id, altitude, slot_inicio)` e
      `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)`
- [ ] `AprovarPlanoUC` em **uma** `@Transactional`: revalida, reserva tudo ou
      não reserva nada
- [ ] `CancelarPlanoUC`: só `APROVADO` com partida futura; libera as reservas
- [ ] UC12 e UC13: consultar altitudes e vagas livres por período
- [ ] **Teste que o enunciado cobra por escrito:** aprovação malsucedida não
      deixa reserva parcial

**Pronto quando:** aprovar duas vezes a mesma altitude no mesmo slot falha na
segunda, e a primeira continua íntegra.

### Frente D — Cadastros e carga inicial (UC02, UC04, UC06, UC08)

Branch: `feat/registration-endpoints` e `db/initial-seed`
**Cadastros são independentes — começam já.** A carga depende de C.

- [ ] `POST /aeroportos`, `POST /aerovias` (sem `PUT`, sem `DELETE` — aerovia é
      imutável), `POST /pilotos`, `POST /aeronaves` (Factory por categoria,
      para o controller não conhecer as subclasses)
- [ ] `data.sql`: planos **aprovados** com suas reservas, gerando ocupação em
      **5 ou mais das 8 aerovias** na data de referência
- [ ] As ocupações são consequência dos planos aprovados — nada de linha solta
      na tabela de reservas
- [ ] Deixar folga: não pode ficar tudo indisponível o dia inteiro, senão não
      dá para demonstrar aprovação bem-sucedida
- [ ] Rodar o roteiro do `CLAUDE.md` §14.4: apaga o banco, sobe, confere as
      contagens, cria/valida/aprova um plano, **reinicia** e confirma que o
      plano continua lá

**Pronto quando:** o banco apagado e recriado do zero dá 5+ aerovias ocupadas
na data de referência, sem nenhuma inserção manual.

### Frente E — Documentação e apresentação (começa agora, não depende de ninguém)

Branch: `docs/adrs-and-readme`

- [ ] Os **8 ADRs** de `docs/arquitetura/decisoes/`. O rascunho de todos já
      está pronto em `logs/decisoes.md`, com a alternativa descartada de cada
      um — é transcrever para o formato do `0000-template.md`
- [ ] `README.md`: os sete `[PREENCHER]` (equipe, data de referência,
      ambiente, execução, banco, URL da API, Swagger)
- [ ] Descomentar o `springdoc-openapi-starter-webmvc-ui` no `pom.xml` com a
      versão compatível com Spring Boot 4.x, e conferir o Swagger UI abrindo
- [ ] `docs/api/requests.http` com as **17 demonstrações** do `CLAUDE.md`
      §16.3, numeradas e comentadas — incluindo as falhas, não só o caminho
      feliz
- [ ] Ensaiar o roteiro na ordem, cronometrado

**Pronto quando:** alguém de fora clona o repositório, segue o README e faz a
primeira requisição sem perguntar nada.

---

## 4. Calendário

| Quando | A | B | C | D | E |
|---|---|---|---|---|---|
| **Qui 15, noite** | começa | — | — | cadastros | ADRs 0001–0003 |
| **Sex 16** | **entrega até meio-dia** | começa à tarde | tabelas e portas | cadastros | ADRs + springdoc |
| **Sáb 17** | apoia B | fecha validação | aprovar e cancelar | `data.sql` | README + `requests.http` |
| **Dom 18, até 18:00** | — | — | fecha | roteiro §14.4 | ensaio |
| **Dom 18, 18:00–23:00** | todos: integração, último PR, conferência final | | | | |

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
