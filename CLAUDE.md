# Guia do repositório

Trabalho Final de Projeto e Arquitetura de Software (PUCRS, 2026/2) — **parte 2**: evolução do
Sistema de Gestão de Planos de Voo para microsserviços. A parte 1 (monólito) está na tag
`parte-1`; as quatro etapas da parte 2, nas tags `etapa-1` a `etapa-4`.

**O enunciado vence.** Onde este guia ou qualquer outro documento divergir de
`docs/enunciado-parte2.md`, o enunciado está certo e o documento é corrigido.

## Onde encontrar as coisas

Este arquivo não repete o que já está documentado — só aponta:

| Preciso de... | Está em |
|---|---|
| Visão geral, como rodar, portas, data de referência | `README.md` |
| O que mudou em cada etapa e como demonstrar | `docs/arquitetura/evolucao-parte-2.md` |
| Decisões de arquitetura (ADRs) | `docs/arquitetura/decisoes/` |
| Modelo de domínio e casos de uso | `docs/dominio/` |
| Contratos HTTP e de mensagens | `docs/api/` |
| Diagramas C4 | `docs/arquitetura/` |
| Padrões de código, nomenclatura, convenção de commit | `docs/padroes-desenvolvimento.md` |
| Pendências e tarefas até a entrega | `docs/PLANO-PARTE2.md` |
| Achados da auditoria de código | `docs/AUDITORIA-PARTE1.md` |

## Regras permanentes

- **Autoria.** Todo commit é assinado pela pessoa que efetivamente fez o trabalho, usando a
  identidade já configurada no Git local (`git config user.name`/`user.email`). Nenhum commit,
  mensagem de PR, comentário ou arquivo do repositório cita ferramenta de IA, Co-Authored-By ou
  link de sessão — mesmo quando uma IA ajudou a escrever o código.
- **Branch e commit.** Sem commit direto na `main`. Branch a partir da `main` atualizada, nomeada
  `<tipo>/<descricao>`; commits pequenos, um assunto por commit, seguindo a convenção já usada no
  `git log` (`docs/padroes-desenvolvimento.md`, seção 1).
- **Sem segredo versionado.** Nada de senha, `.env`, banco H2 local (`data/`) ou arquivo de build
  no Git — o `.gitignore` já cobre isso; não adicionar exceção sem necessidade real.
- **Regra de dependência da hexagonal.** `domain/` de `backend`, `cadastro` e `registro` não
  importa Spring, JPA nem `infra`. O CI confere isso a cada push.
- **Mudança de regra de negócio ou de contrato de API** precisa de decisão da equipe antes do
  commit, não só de uma correção isolada — registrar em ADR quando afetar arquitetura.
- **Todo integrante precisa saber defender o trabalho inteiro**, não só a parte que implementou —
  é critério de entrega (seção 6 do enunciado da parte 2).
