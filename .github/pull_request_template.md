## Descrição da Mudança

> Adicione um resumo detalhado do que esta alteração engloba. Comece pelo
> problema que existia, não pela solução.

**Caso(s) de uso:** <!-- ex.: UC09 (validar plano), UC10 (aprovar plano) — ver CLAUDE.md seção 7 -->

**Entrega do cronograma:** <!-- ex.: 14/10 — implementação de 40% dos casos de uso -->

## Tipo de Mudança

- [ ] Feature - Adição de nova funcionalidade ou caso de uso
- [ ] Bugfix - Correção de bug
- [ ] Refactoring - Refatoração de código
- [ ] Database - Schema, migration, carga inicial, índice ou constraint
- [ ] Documentation - Diagrama (domínio, C4), ADR ou README
- [ ] Test - Apenas testes
- [ ] Chore - Tarefas de manutenção, build, dependência
- [ ] Infrastructure - Docker, CI, ambiente de execução

## Validação de Arquitetura

- [ ] `domain/` não importa `infra`, `org.springframework` nem `jakarta.persistence` (nem anotação).
- [ ] Caso de uso novo tem porta em `domain/ports/in`, com o sufixo `UseCase`.
- [ ] Acesso a dado novo passa por porta em `domain/ports/out`, implementada em `infra/adapters/out/persistence`.
- [ ] Controller só traduz HTTP: sem regra de negócio, sem consulta ao banco.
- [ ] Entrada e saída da API são DTOs (`...Request` / `...Response`), nunca a entidade de domínio serializada.
- [ ] Regra que varia por categoria de aeronave é polimórfica, sem `instanceof` nem `switch` por categoria.
- [ ] Operação com múltiplas alterações (aprovação, cancelamento) está em uma única transação.
- [ ] Classe nova está no pacote correspondente ao seu sufixo (ver `docs/padroes-desenvolvimento.md`, seção 2.1).

## Banco de Dados

- [ ] Não se aplica.
- [ ] Alteração de schema acompanhada do script correspondente.
- [ ] Script roda do zero, em banco vazio, sem erro.
- [ ] Chaves, índices e constraints revisados (PK, FK, `UNIQUE`, `NOT NULL`).
- [ ] `UNIQUE` de reserva preservado: `(aerovia, altitude, slot)` e `(aeroporto, vaga, slot)`.
- [ ] Carga inicial continua válida: 4 aeroportos, 8 aerovias, aeronaves das 3 categorias.
- [ ] Data de referência ainda com ocupação em **5 ou mais** das 8 aerovias.
- [ ] Dados sobrevivem à reinicialização da aplicação.

## Padrões de Código

- [ ] Branch em kebab-case no formato `<tipo>/<descricao>`, em inglês e **sem ID de task**
      (ex. `feat/flight-plan-validation`, `db/initial-airway-seed`).
- [ ] Commits seguindo Conventional Commits (`<tipo>: <descricao>`).
- [ ] Título do PR no formato `[TIPO] Descrição objetiva`.
- [ ] Classes em `PascalCase`, arquivo com o mesmo nome da classe pública.
- [ ] Métodos e variáveis em `camelCase`; booleano com prefixo `is`/`has`/`can`/`should` ou `pode`/`possui`.
- [ ] Constantes em `UPPER_SNAKE_CASE`; valores de enum em `UPPER_SNAKE_CASE`.
- [ ] Idioma respeitado: domínio em português, infraestrutura em inglês.
- [ ] Injeção de dependência por construtor, não por campo.

## Testes

- [ ] Testes adicionados ou atualizados para o que mudou.
- [ ] Teste de regra de domínio roda **sem subir o Spring e sem banco**.
- [ ] Classe de teste nomeada `<Classe>Test`, no espelho do pacote em `src/test`.
- [ ] `./mvnw clean test` passando localmente.
- [ ] Se mexeu em slot, duração, autonomia ou altitude: caso de borda coberto.
- [ ] Se mexeu em aprovação: coberto que a falha **não deixa reserva parcial**.

## Checklist

- [ ] PR aberto contra `main`.
- [ ] Branch sem conflitos com `main`.
- [ ] `./mvnw clean package` passando.
- [ ] Autorrevisão da seção 11 do `CLAUDE.md` feita.
- [ ] Diagrama de domínio atualizado, se a mudança mexeu no domínio.
- [ ] Diagramas C4 atualizados, se a mudança mexeu na estrutura.
- [ ] ADR escrito, se houve decisão arquitetural (`docs/arquitetura/decisoes/`).
- [ ] `README.md` atualizado se for o caso (execução, carga, data de referência).
- [ ] Entradas feitas em `logs/log.md` e, se houve decisão, em `logs/decisoes.md`.
- [ ] Sem comentários desnecessários (e sem emojis).
- [ ] Sem credenciais, tokens ou dados sensíveis no diff.
- [ ] `git status` mostra só os arquivos desta mudança.

## Verificação Manual

> Requisições executadas para validar a mudança (método, rota, corpo, resposta
> esperada). Mantenha-as também em `docs/api/requests.http`.

1.

**O que NÃO foi testado:**

## Observações

> Decisão que um revisor vai questionar, já respondida. Limitação conhecida e
> de quem depende a correção.
