# Sistema de Gestão de Planos de Voo

Trabalho Final (parte 1) de **Projeto e Arquitetura de Software** — PUCRS,
turma 340, 2026/2. Backend em Java + Spring Boot, com API REST e persistência
em banco de dados.

O sistema permite montar um plano de voo trecho a trecho (aerovia, altitude,
horário de entrada e o que acontece no aeroporto de destino do trecho),
**verificar** se o percurso é contínuo, se os horários batem com a duração
calculada, se a aeronave pode operar naquela altitude e naquele horário, se a
autonomia aguenta e se altitude e vaga estão livres — e então **reservar** tudo
de uma vez e aprovar.

## Equipe

- Antônio Costa
- Gustavo Both
- João Pedro Bianchi
- Pedro Redivo
- Vicenzo Masera

## Data de referência

**19/10/2026.**

É a data usada nos testes de disponibilidade, conflito e reserva durante a
apresentação. A carga inicial traz **quatro planos já aprovados** voando nesse
dia, e as reservas deles ocupam altitudes de aerovias e vagas de aeroportos:
**6 das 8 aerovias** têm ocupação, acima do mínimo de 60% que o enunciado pede.

Dois desses planos partem de madrugada e de manhã (01:00 e 06:30) e dois à
noite (20:00 e 21:00). No dia da apresentação, os primeiros já partiram e
demonstram que um plano cuja partida ocorreu **não** é cancelável; os últimos
ainda são futuros e demonstram o cancelamento com liberação das reservas.

A R1 e a R3 ficam livres o dia inteiro de propósito, para que sempre haja
recurso disponível para demonstrar uma aprovação bem-sucedida.

## Preparação do ambiente

Só é preciso ter **Java 25** instalado. O Maven vem junto, pelo wrapper
(`mvnw`), e o banco é embutido — não há Docker, serviço externo nem credencial
a configurar.

```sh
java -version   # precisa ser 25 ou superior
```

Se não tiver o Java 25: https://adoptium.net ou, no macOS,
`brew install openjdk@25`.

## Execução

```sh
cd backend
./mvnw spring-boot:run
```

A aplicação sobe em **http://localhost:8080**. O primeiro start demora mais,
porque o Maven baixa as dependências.

Para compilar e rodar os testes:

```sh
cd backend
./mvnw clean test
```

Para gerar o `.jar`:

```sh
cd backend
./mvnw clean package
java -jar target/planos-de-voo-0.0.1-SNAPSHOT.jar
```

## Banco e carga inicial

O banco é **H2 em modo arquivo**, gravado em `backend/data/planosdevoo.mv.db`.
É arquivo e não memória de propósito: o enunciado exige que as alterações
feitas pela API sobrevivam à reinicialização da aplicação.

Na inicialização, o Spring executa dois scripts versionados, nesta ordem:

| Script | O que faz |
|---|---|
| `backend/src/main/resources/db/schema.sql` | cria as tabelas, com chaves, índices e restrições |
| `backend/src/main/resources/db/data.sql` | carrega a malha aérea, os pilotos, as aeronaves e os planos aprovados com suas reservas |

O schema **não** é gerado pelo Hibernate (`spring.jpa.hibernate.ddl-auto=none`),
para que seja versionável e reproduzível. O `data.sql` usa `MERGE`, então rodar
a aplicação várias vezes não duplica os dados.

### Recomeçar do zero

```sh
cd backend
rm -rf ./data
./mvnw spring-boot:run
```

A carga inicial é reproduzível a partir deste repositório: não há nenhuma
inserção manual necessária antes da apresentação. O `data.sql` é conferido
contra o próprio domínio a cada build, pelo `SeedConsistenciaTest`: se uma linha
de reserva deixar de bater com o que a aprovação geraria, o build quebra.

### Inspecionar o banco

Console web do H2, com a aplicação no ar: **http://localhost:8080/h2-console**

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/planosdevoo` |
| Usuário | `sa` |
| Senha | *(em branco)* |

## API

URL base: **http://localhost:8080**

| O que | Onde |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Documento OpenAPI | http://localhost:8080/v3/api-docs |
| Roteiro de requisições | [`docs/api/requests.http`](docs/api/requests.http) |

O `requests.http` cobre o ciclo completo de um plano e os casos de falha, na
ordem da apresentação, com o resultado esperado comentado em cada bloco. Abre
direto no VS Code (extensão REST Client) ou no IntelliJ.

Respostas de erro seguem o formato **Problem Detail** (RFC 7807), com a lista
de problemas quando há mais de um:

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Requisição inválida",
  "problemas": ["origem: must not be blank", "partida: must not be null"]
}
```

| Situação | Status |
|---|---|
| Consulta bem-sucedida | 200 |
| Criação de plano | 201 |
| Recurso inexistente | 404 |
| Operação não permitida no estado atual do plano | 409 |
| Corpo da requisição inválido | 400 |
| Regra de negócio violada | 422 |

## Documentação

| O que | Onde |
|---|---|
| Modelo de domínio (UML) | [`docs/dominio/`](docs/dominio/) |
| Casos de uso UC01–UC14 | [`docs/dominio/casos-de-uso.md`](docs/dominio/casos-de-uso.md) |
| Diagramas C4 (contexto, containers, componentes) | [`docs/arquitetura/`](docs/arquitetura/) |
| Decisões arquiteturais (ADR) | [`docs/arquitetura/decisoes/`](docs/arquitetura/decisoes/) |
| Padrões de desenvolvimento | [`docs/padroes-desenvolvimento.md`](docs/padroes-desenvolvimento.md) |
| Plano de trabalho da equipe | [`docs/plano-de-trabalho.md`](docs/plano-de-trabalho.md) |
| Guia de contexto do repositório | [`CLAUDE.md`](CLAUDE.md) |
| Enunciado | `PAS_TF_2026_2_PlanosDeVoo (1).md` |

## Arquitetura, em uma olhada

**Hexagonal (ports & adapters)**, documentada em **C4**. O domínio não conhece
Spring, JPA nem HTTP; quem depende é sempre a infraestrutura.

```
backend/src/main/java/com/pucrs/planosdevoo/
├── domain/
│   ├── model/              entidades e value objects — Java puro
│   └── ports/
│       ├── in/usecases/    casos de uso
│       └── out/repositories/ interfaces de repositório
└── infra/
    ├── adapters/
    │   ├── in/rest/        controllers e DTOs
    │   └── out/persistence/ entidades JPA e mapeadores
    └── config/             composição dos beans
```

A regra de dependência é verificável em uma linha — tem que devolver nada:

```sh
grep -rn "org.springframework\|jakarta.persistence\|infra\." \
  backend/src/main/java/com/pucrs/planosdevoo/domain/
```

As decisões por trás dessa organização, com as alternativas que foram
descartadas, estão em [`docs/arquitetura/decisoes/`](docs/arquitetura/decisoes/).
