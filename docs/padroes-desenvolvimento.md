# Padrões de desenvolvimento

Adaptado do guia equivalente da parte 1 (`trabalho-1/docs/padroes-desenvolvimento.md`) para os
cinco módulos da parte 2 (`backend`, `cadastro`, `registro`, `gateway`, `eureka`) e para os sufixos
de classe que o código realmente usa hoje — que mudaram da parte 1 para a parte 2 em alguns pontos
(ver seção 2.1).

Branch de integração: **`main`**.

---

## 1. Git

| Elemento | Formato / Regra | Exemplos |
|---|---|---|
| **Branch** | `<tipo>/<descricao>` em `kebab-case`, em inglês, sem ID de task | `feat/registration-endpoints` · `infra/docker-containerization` |
| **Commit** | `<tipo>: <descricao>`, Conventional Commits, inglês, imperativo, minúsculo, sem ponto final | `feat: add the air network service with its own database` |

### 1.1 Tipos

| Tipo | Quando usar |
|---|---|
| `feat` | caso de uso, endpoint ou funcionalidade nova |
| `fix` | correção de defeito |
| `refactor` | muda a estrutura sem mudar o comportamento |
| `db` | schema, carga inicial, índice, constraint |
| `docs` | diagrama, ADR, README, este arquivo |
| `test` | só testes |
| `chore` | build, dependência, configuração, `.gitignore` |
| `infra` | Docker, CI, Eureka, Gateway, RabbitMQ, ambiente de execução |

Um commit, uma intenção. Confira o próprio `git log` antes de inventar um tipo novo.

### 1.2 Regras

- Branch sai de `main` atualizada; PR aberto contra `main`, sem conflito.
- Commit nunca inclui `target/`, `.DS_Store`, `.env`, banco local (`data/`) nem segredo.
- **Autoria dos commits:** quem efetivamente escreveu o código assina o commit — inclusive
  quando uma ferramenta de IA ajudou a escrever. Nenhum commit, mensagem de PR ou arquivo do
  repositório deve citar a ferramenta usada.

---

## 2. Nomenclatura Java

| Elemento | Formato | Exemplo |
|---|---|---|
| Classe / interface / record | `PascalCase` | `PlanoDeVoo`, `MalhaAereaPort` |
| Arquivo | `PascalCase.java`, nome da classe pública | `PlanoDeVoo.java` |
| Pacote | minúsculas, sem `_` nem hífen | `com.pucrs.planosdevoo.domain.model` |
| Método | `camelCase`, começa com verbo | `adicionarTrecho()`, `slotsOcupados()` |
| Variável e parâmetro | `camelCase`, descritivo | `velocidadeCruzeiroKmh` |
| Booleano | prefixo `is`/`has`/`can`/`should`, ou `pode`/`possui` | `podeOperar()`, `possuiVaga()` |
| Constante | `UPPER_SNAKE_CASE` | `RabbitConfig.EXCHANGE` |
| Enum | `PascalCase`, valores `UPPER_SNAKE_CASE` | `SituacaoPlano.EM_EDICAO` |
| Classe de teste | `<Classe>Test` | `SlotTest.java` |

### 2.1 Sufixos da hexagonal (como o código está hoje)

Cada módulo de negócio (`backend`, `cadastro`, `registro`) segue a mesma árvore:

```
domain/
  model/        entidades e value objects
  exceptions/   exceções de negócio
  services/     implementam as portas de entrada
  ports/in/     interfaces *UseCase e *Command
  ports/out/    interfaces *Repository e *Port
infra/
  adapters/in/rest/         controllers, dto/ e RestExceptionHandler
  adapters/in/messaging/    consumidor RabbitMQ (só no registro)
  adapters/out/persistence/ entity/, repository/, mapper/
  adapters/out/http/        cliente HTTP de outro serviço (só no backend)
  adapters/out/messaging/   publicador de mensagens (só no backend)
  config/
```

| Papel | Sufixo | Pacote | Exemplo |
|---|---|---|---|
| Porta de entrada (caso de uso) | `UseCase` | `domain.ports.in` | `PlanoDeVooUseCase` |
| Parâmetros de um caso de uso | `Command` | `domain.ports.in` | `PlanoCommand` |
| Implementação da porta de entrada | `Service` | `domain.services` | `PlanoDeVooService` |
| Porta de saída de persistência | `Repository` | `domain.ports.out` | `PlanoDeVooRepository` |
| Outra porta de saída | `Port` | `domain.ports.out` | `MalhaAereaPort`, `TransacaoPort` |
| Adaptador de entrada HTTP | `Controller` | `infra.adapters.in.rest` | `PlanoDeVooController` |
| DTO de entrada / saída | `Request` / `Response` | `infra.adapters.in.rest.dto` | `PlanoRequest`, `PlanoResponse` |
| Entidade de persistência | `Entity` | `infra.adapters.out.persistence.entity` | `PlanoDeVooEntity` |
| Repositório Spring Data | `JpaRepository` | `infra.adapters.out.persistence.repository` | `PlanoDeVooJpaRepository` |
| Implementação da porta de saída | `RepositoryAdapter` | `infra.adapters.out.persistence.repository` | `PlanoDeVooRepositoryAdapter` |
| Mapeador domínio ↔ persistência | `Mapper` | `infra.adapters.out.persistence.mapper` | `PlanoDeVooMapper` |
| Cliente HTTP de outro serviço | `HttpAdapter` | `infra.adapters.out.http` | `MalhaAereaHttpAdapter` |
| Publicador / consumidor de mensagens | `Publisher` / `Listener` | `infra.adapters.out.messaging` / `in.messaging` | `AprovacaoRabbitPublisher`, `AprovacaoListener` |
| Bean de configuração | `Config` | `infra.config` | `RabbitConfig`, `CadastroClientConfig` |

Isto difere da parte 1 em dois pontos, de propósito: a interface de caso de uso é `...UseCase`
(não `...UC`) e o DTO é `...Request`/`...Response` (não `...RequestDTO`/`...ResponseDTO`) — os
pacotes `infra.adapters.in.rest.dto` já dizem "é DTO", o sufixo repetiria a informação.

`gateway` e `eureka` não seguem essa árvore: são configuração pura (`application.properties`),
sem domínio próprio.

### 2.2 Idioma do código

Ver ADR 0016. Resumo: domínio em português (vocabulário do enunciado), infraestrutura em inglês
(convenções do framework), branch e commit em inglês, documentação e ADR em português.

---

## 3. Regras de arquitetura

- `domain/` de `backend`, `cadastro` e `registro` não importa Spring, JPA nem `infra`. O CI
  confere isso a cada push (`.github/workflows/ci.yml`).
- Controller só traduz HTTP: valida formato, chama a porta de entrada, devolve DTO. Sem regra de
  negócio e sem consulta direta a repositório.
- Entidade de domínio nunca é serializada direto: sempre um `Request`/`Response`.
- Detalhe de integração entre serviços (HTTP, mensageria, descoberta, timeout, DTO de transporte)
  fica concentrado no adaptador (`HttpAdapter`, `Publisher`, `Listener`) — nunca no domínio.
- A aprovação de um plano é uma transação (`TransacaoPort`): grava tudo ou nada.
- Injeção de dependência por construtor, não por campo.

---

## 4. Comandos

| O que | Comando |
|---|---|
| Compilar e testar um módulo | `cd <modulo> && ./mvnw clean test` |
| Subir a stack completa | `docker compose up -d --build --wait --wait-timeout 900` |
| Rodar um módulo fora do Docker | `cd <modulo> && ./mvnw spring-boot:run` |

Detalhes de ambiente, portas e data de referência: `README.md` da raiz.
