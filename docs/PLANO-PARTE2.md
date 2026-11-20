# Plano até a entrega (29/11)

Levantamento do que falta, cruzando o estado real do código (ver `docs/AUDITORIA-PARTE1.md`) com
as seções 2 a 7 do enunciado (`docs/enunciado-parte2.md`), e proposta de distribuição de tarefas.
**É proposta — o grupo ajusta.**

Hoje é 19/11 (quinta). Prazo: domingo 29/11, 23:55. Apresentação nas aulas de acompanhamento (por
etapa) ou tudo em 30/11 — decisão em aberto, ver cronograma.

## Quem é quem

Pelo `git shortlog -sne` (e-mails duplicados da mesma pessoa unificados) e pelo `README.md`:

| Membro | Commits até aqui | E-mails no Git |
|---|---|---|
| Antônio Costa | 58 | antonio.lc119@gmail.com, 102228529+antoniocosta0@users.noreply.github.com |
| Vicenzo Masera | 45 | vimasera11@gmail.com |
| Pedro Redivo Ferreira | 6 | pedro.redivo@gmail.com |
| Gustavo Both | 6 | gustakilo@gmail.com, gustavobothr@gmail.com |
| João Pedro Bianchi | 5 | joao.bianchi06@gmail.com |

**Isto importa pra distribuição:** o Vicenzo implementou as quatro etapas da parte 2
praticamente sozinho (commits de 11/11 a 15/11). Isso significa código bom e testado, mas também
um risco de entrega: o enunciado cobra que **qualquer um do grupo** saiba defender qualquer etapa,
e hoje só o Vicenzo (e, pela parte 1, o Antônio) navegam o código com familiaridade. O plano abaixo
prioriza fechar essa distância antes de fechar lacuna de funcionalidade — porque funcionalidade já
está, em grande parte.

## 1. Tabela de pendências por etapa

| Item do enunciado | Situação | Evidência |
|---|---|---|
| **Etapa 1** — diagnóstico de acoplamentos, portas e limites transacionais | **Feito** | `docs/arquitetura/evolucao-parte-2.md`, ADRs 0010–0012, tag `etapa-1` |
| **Etapa 2** — Gateway + Eureka ao redor do monólito | **Feito** | ADR 0013, tag `etapa-2`, `gateway/`, `eureka/` |
| **Etapa 3** — cadastro extraído, integração HTTP | **Feito** | ADR 0014, tag `etapa-3`, `cadastro/` |
| Etapa 3 — distinção 404 × indisponibilidade, timeout configurado | **Feito** | `MalhaAereaHttpAdapter`, `CadastroClientConfig` |
| **Etapa 4** — outbox, RabbitMQ, consumidor idempotente | **Feito** | ADR 0015, tag `etapa-4`, `registro/` |
| Seção 5 — 60% das aerovias ocupadas na carga inicial | **Feito** | `backend/.../db/data.sql` (75%, 6 de 8) |
| Seção 5 — planos da carga chegam ao registro pela fila, sem duplicar | **Feito** | `outbox_evento` com `eventoId` fixo no `data.sql` |
| Seção 6 — código, config, scripts de inicialização | **Feito** | `compose.yaml`, `db/schema.sql` + `data.sql` de cada serviço |
| Seção 6 — contratos HTTP e de mensagens documentados | **Feito** | `docs/api/cadastro.md`, `docs/api/mensagens.md` |
| Seção 6 — modelos de domínio atualizados | **Feito** | `docs/dominio/modelo-*.puml` (gestão, cadastro, registro) |
| Seção 6 — C4 contexto/contêineres/componentes | **Feito** | `docs/arquitetura/c4-*.puml/.png` |
| Seção 6 — registro de decisões | **Feito** | 17 ADRs, índice em `docs/arquitetura/decisoes/README.md` |
| Seção 6 — testes das regras preservadas | **Feito** | 62 testes no `backend`, cobrindo parte 1 + propriedade + outbox |
| Seção 6 — testes dos **novos comportamentos de integração** | **Parcial** | `cadastro` e `registro` só têm teste de serviço de domínio; falta teste de controller/`@SpringBootTest` |
| Seção 6 — `README.md` com build, execução, demonstração | **Feito** | `README.md` da raiz |
| Seção 7 — as 11 demonstrações, roteiro escrito | **Feito (roteiro)** | tabela em `docs/arquitetura/evolucao-parte-2.md` |
| Seção 7 — as 11 demonstrações, **ensaiadas com a stack via Docker Compose** | **Falta verificar** | não validado nesta sessão (Docker indisponível no ambiente da auditoria) |
| Capacidade de qualquer integrante defender o trabalho inteiro | **Falta** | só Vicenzo e Antônio têm familiaridade ampla com o código atual |

Não há pendência de funcionalidade bloqueando a entrega. O que falta é: fechar a lacuna de teste
de integração em dois serviços, confirmar a stack completa rodando de ponta a ponta, e — o item
que mais pesa no prazo — nivelar o conhecimento do grupo para a apresentação.

## 2. Distribuição de tarefas

Pensada para: (a) quem tem menos familiaridade com uma etapa estuda e prepara exatamente essa
etapa, sempre em par com quem a implementou; (b) a Etapa 1 já destravou as demais, então etapas 2,
3 e 4 (e a validação de infraestrutura) andam em paralelo a partir de agora.

| Quem | Tarefa | Depende de | Prazo sugerido |
|---|---|---|---|
| **Pedro** | Estudar a etapa 2 (Gateway + Eureka) a fundo: ler `gateway/`, `eureka/`, ADR 0013, rodar os cenários de "destino indisponível" do roteiro. Preparar-se para conduzir essa parte da demonstração. | — | 23/11 |
| **João** | Estudar a etapa 3 (cadastro) a fundo: ler `cadastro/`, ADR 0014, `docs/api/cadastro.md`. Escrever o teste de integração HTTP que falta (`@SpringBootTest` ou `@WebMvcTest` dos controllers de `cadastro`, cobrindo 201/404/409). Preparar-se para conduzir essa parte. | Etapa 1 (pronta) | 25/11 |
| **Gustavo** | Estudar a etapa 4 (RabbitMQ + registro) a fundo: ler `registro/`, ADR 0015, `docs/api/mensagens.md`. Escrever o teste de integração HTTP que falta no `registro` (consulta por id e listagem). Preparar-se para conduzir essa parte. | Etapa 1 (pronta) | 25/11 |
| **Vicenzo** | Apoio técnico às três tarefas acima (é quem conhece o código); validar a stack completa com `docker compose up` e rodar as 11 demonstrações do §7 de ponta a ponta, anotando o que não bateu com `docs/arquitetura/evolucao-parte-2.md`. | — | 23/11 (validação), contínuo (apoio) |
| **Antônio** | Coordenar a integração das branches, revisar os PRs, decidir com o grupo o formato de apresentação (por etapa ou 30/11), organizar o ensaio geral e manter `docs/PLANO-PARTE2.md` e o README atualizados até a entrega. | — | contínuo |

Responsáveis por manter atualizado o que já existe (quem tocar no módulo correspondente):

| Artefato | Responsável por manter atualizado |
|---|---|
| Tags `etapa-1`–`etapa-4` e commits de cada etapa | já fechadas; só Pedro/João/Gustavo criam commit novo se mudarem algo na etapa que estudaram |
| `README.md` | Antônio |
| Diagramas C4 (contexto, contêineres, componentes) | quem alterar a arquitetura de algum serviço durante os testes de integração |
| Registro de decisões (ADRs) | quem tomar uma decisão nova durante o fechamento (ex.: decisão do H2 console, se mudar) |
| Testes | João (cadastro), Gustavo (registro); Vicenzo e Antônio revisam |

## 3. Cronograma

| Quando | O quê |
|---|---|
| 19–21/11 (qui–sáb) | Estudo individual por etapa (tabela acima). Vicenzo roda a stack completa e valida as 11 demonstrações. |
| 22–23/11 (dom–seg) | Testes de integração HTTP de `cadastro` e `registro` (João, Gustavo), com apoio do Vicenzo. Correções que a validação do Vicenzo tiver apontado. |
| 24/11 (ter) | Decisão do grupo: apresentar por etapa nas aulas de acompanhamento ou tudo em 30/11. Reflete no que precisa estar pronto antes de cada aula. |
| 25–26/11 (qua–qui) | **Ensaio geral** das 11 demonstrações, com a stack via Docker Compose, cada um conduzindo a etapa que estudou. Antônio e Vicenzo observam e cronometram. |
| 27/11 (sex) | Revisão cruzada: cada um explica, em 5 minutos, a etapa de **outro** colega — é o teste real de "todo mundo defende o trabalho inteiro". Ajusta quem travar. |
| 28/11 (sáb) | Buffer: pendência do ensaio, revisão final de README e diagramas, `git log` limpo nas branches. |
| 29/11 (dom), até 23:55 | PR final mergeado na `main`, tags conferidas, entrega. |
| 30/11 (se decidido) | Apresentação completa, se o grupo optar por não apresentar por etapa nas aulas de acompanhamento. |

## 4. Riscos principais

| Risco | Estado atual | Mitigação |
|---|---|---|
| **Outbox transacional** não recuperar uma aprovação após queda do broker/produtor | Implementado (`AprovacaoOutboxAdapter` grava na transação; `AprovacaoRabbitPublisher` só marca publicado após confirmação) | Demonstração #8 do roteiro (parar RabbitMQ e o `backend`, religar, confirmar publicação atrasada) precisa ser ensaiada, não só lida no código |
| **Idempotência do consumidor** falhar sob reentrega | Implementado (checagem + `UNIQUE` em `evento_id`/`plano_id` no `registro`) | Ensaiar demonstração #9 (reenviar a mesma mensagem pelo painel do RabbitMQ) antes da apresentação |
| **Carga inicial duplicar** a cada reinício | Implementado (`MERGE ... WHEN NOT MATCHED` em todos os `data.sql`) | Qualquer PR que tocar em `data.sql` precisa reexecutar o roteiro de reinício (seção 14.4 do guia da parte 1) antes de mergear |
| **60% das aerovias ocupadas via mensagens**, não por inserção direta | Implementado (planos da carga aprovados normalmente, outbox com `eventoId` fixo) | Mesma mitigação acima: qualquer mudança na carga do `backend` ou do `cadastro` precisa preservar essa cadeia |
| Grupo não conseguir defender etapas que não implementou | Risco real hoje (3 de 5 membros com pouca familiaridade com a parte 2) | É o foco do cronograma acima — estudo individual + ensaio + revisão cruzada |

## 5. Pendências técnicas menores (da auditoria)

Da `docs/AUDITORIA-PARTE1.md`, atribuídas junto com o estudo de cada etapa:

- Teste de integração HTTP do `cadastro` → João.
- Teste de integração HTTP do `registro` → Gustavo.
- Decidir se o H2 console do `backend` fica habilitado na entrega final, ou se é restrito a um
  profile de desenvolvimento → decisão do grupo, Antônio leva a discussão.
- Validar a stack completa via `docker compose up -d --build --wait` → Vicenzo, primeira coisa do
  cronograma.
