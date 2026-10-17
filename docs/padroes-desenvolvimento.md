# Padrões de desenvolvimento — `trabalho-1`

Adaptado dos padrões usados no projeto de AGES II, trocando React/TS e Python
por **Java 25 + Spring Boot**, e acrescentando os sufixos da arquitetura
hexagonal (ver `CLAUDE.md`, seção 8).

Branch de integração: **`main`**.

---

## 1. Git

| Elemento | Formato / Regra | Exemplos |
|---|---|---|
| **Branch** | `<tipo>/<descricao>` em `kebab-case`, **em inglês**, **sem ID de task** | `feat/flight-plan-validation` · `fix/slot-boundary-calculation` · `docs/c4-diagrams` |
| **Commit** | `<tipo>: <descricao>` seguindo Conventional Commits, em inglês, imperativo, minúsculo, sem ponto final | `feat: add flight plan approval use case` |
| **Nome do PR** | `[TIPO] Descrição objetiva`, em português | `[FEAT] Adiciona aprovação de plano de voo` |

### 1.1 Tipos

| Tipo | Branch / Commit | PR | Quando usar |
|---|---|---|---|
| Feature | `feat` | `[FEAT]` | caso de uso ou funcionalidade nova |
| Bugfix | `fix` | `[FIX]` | correção de defeito |
| Refactoring | `refactor` | `[REFACTOR]` | muda a estrutura sem mudar o comportamento |
| Database | `db` | `[DB]` | schema, migration, carga inicial, índice, constraint |
| Documentation | `docs` | `[DOCS]` | diagrama (domínio, C4), ADR, README, este arquivo |
| Test | `test` | `[TEST]` | só testes |
| Chore | `chore` | `[CHORE]` | build, dependência, configuração, `.gitignore` |
| Infrastructure | `infra` | `[INFRA]` | Docker, CI, ambiente de execução |

Um commit, uma intenção. Se a mensagem precisa de "and" duas vezes, são dois
commits.

### 1.2 Regras

- **Sem ID de task no nome da branch.** Este trabalho não usa ferramenta de
  tasks; o rastro é o caso de uso (UC01–UC14, `CLAUDE.md` seção 7) e a entrega
  do cronograma, citados no corpo do PR.
- Branch sai sempre de `main` atualizada.
- PR é aberto contra `main`, sem conflito.
- Commit nunca inclui `target/`, `.DS_Store`, `.env` ou banco local.

---

## 2. Nomenclatura Java

| Elemento | Formato | Regra | Exemplos |
|---|---|---|---|
| **Classe / interface / record** | `PascalCase` | nome representando entidade ou responsabilidade | `PlanoDeVoo` · `AprovarPlanoUseCase` |
| **Arquivo** | `PascalCase.java` | mesmo nome da classe pública | `PlanoDeVoo.java` |
| **Pacote** | minúsculas, sem `_` nem hífen | um segmento por nível da arquitetura | `com.<grupo>.planosdevoo.domain.model` |
| **Método** | `camelCase` | preferencialmente iniciar com verbo | `calcularChegada()` · `slotsOcupados()` |
| **Variável e parâmetro** | `camelCase` | nome descritivo; evitar abreviação sem necessidade | `velocidadeCruzeiroKmh` |
| **Booleano (método ou variável)** | `camelCase` | prefixo `is`, `has`, `can`, `should` — ou o equivalente em português (`pode`, `possui`, `esta`) | `podeOperar()` · `possuiVaga()` · `isAprovado` |
| **Constante** | `UPPER_SNAKE_CASE` | `static final`; valores fixos compartilhados | `MARGEM_SEGURANCA` · `ALTITUDE_MINIMA_PES` |
| **Variável local `final`** | `camelCase` | não usar `UPPER_SNAKE_CASE` só por ser `final` | `final var chegada = ...` |
| **Enum** | `PascalCase` | valores em `UPPER_SNAKE_CASE` | `SituacaoPlano.EM_EDICAO` |
| **Classe de teste** | `<Classe>Test` | ao lado da classe testada, no espelho de `src/test` | `SlotTest.java` |
| **Variável de ambiente** | `UPPER_SNAKE_CASE` | nome explícito, **sem valor sensível no código** | `DATABASE_URL` |

### 2.1 Sufixos da arquitetura hexagonal

Cada papel tem um sufixo, e o sufixo diz em que pasta a classe mora:

| Papel | Sufixo | Pacote | Exemplo |
|---|---|---|---|
| Porta de entrada (caso de uso) | `UseCase` | `domain.ports.in` | `AprovarPlanoUseCase` |
| Implementação do caso de uso | `Service` | `domain.servicos` | `AprovarPlanoService` |
| Porta de saída | `Repository` | `domain.ports.out` | `PlanoRepository` |
| Adaptador de saída | prefixo da tecnologia + `Repository` | `infra.adapters.out.persistence` | `JpaPlanoRepository` |
| Entidade de persistência | sufixo `Entity` | `infra.adapters.out.persistence` | `PlanoEntity` |
| Mapeador domínio ↔ persistência | sufixo `Mapper` | `infra.adapters.out.persistence` | `PlanoMapper` |
| Adaptador de entrada | `Controller` | `infra.adapters.in.rest` | `PlanoController` |
| DTO de entrada | `Request` | `infra.adapters.in.rest.dto` | `CriarPlanoRequest` |
| DTO de saída | `Response` | `infra.adapters.in.rest.dto` | `PlanoResponse` |
| Bean de configuração | `Config` | `infra.config` | `BeansConfig` |

Classe sem sufixo reconhecível em pasta de infraestrutura é sinal de que o
papel dela não foi pensado.

### 2.2 Idioma do código

**Divergência consciente do padrão de AGES II**, que manda tudo em inglês:

| Camada | Idioma | Por quê |
|---|---|---|
| `domain/` (entidades, value objects, portas, casos de uso) | **português** | é a linguagem ubíqua do DDD e o vocabulário do enunciado e do professor: `PlanoDeVoo`, `Aerovia`, `Trecho`, `Pouso`, `sobrevoo` |
| `infra/` (adaptadores, config, nomes técnicos) | **inglês** | `JpaPlanoRepository`, `findById`, `save` |
| Branch e commit | **inglês** | mantém o padrão de AGES II |
| Título e corpo do PR, documentação, ADR | **português** | é o que o professor lê |

Traduzir `PlanoDeVoo` para `FlightPlan` quebraria a correspondência direta
entre o enunciado, o diagrama de domínio e o código — que é exatamente o que a
avaliação cobra ("coerência entre domínio, arquitetura, documentação e
implementação"). Esta decisão está registrada como ADR.

O que **não** vale: misturar dentro do mesmo pacote. `PlanoDeVoo.getFlightId()`
é o pior dos dois mundos.

---

## 3. Regras de arquitetura

As que o PR cobra (ver `CLAUDE.md` seção 8):

- `domain/` **não importa** `infra`, `org.springframework`, `jakarta.persistence`
  nem nada de tecnologia. Nem anotação.
- `infra/` conhece `domain`. A seta é sempre para dentro.
- Controller só traduz HTTP: valida formato de entrada, chama o caso de uso,
  mapeia erro para status, devolve DTO. **Sem regra de negócio.**
- Regra de negócio mora na entidade de domínio ou no serviço de domínio; o caso
  de uso **orquestra**, não decide.
- Entidade de domínio nunca é serializada direto na resposta: sempre um DTO.
- Regra que varia por categoria de aeronave é **polimorfismo**, nunca
  `instanceof` ou `switch` por categoria espalhado.
- A aprovação de um plano é **uma** transação: ou grava todas as reservas, ou
  nenhuma.

---

## 4. Comandos

| O que | Comando |
|---|---|
| Compilar e testar | `./mvnw clean test` |
| Empacotar | `./mvnw clean package` |
| Executar | `./mvnw spring-boot:run` |
| Teste de uma classe | `./mvnw test -Dtest=SlotTest` |

Tudo isso roda **antes** de abrir o PR, e o resultado vai no checklist.
