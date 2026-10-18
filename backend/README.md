# backend — estrutura de pastas

Entrega de **07/10**. A organização segue o estudo de caso de arquitetura
hexagonal da cadeira: no primeiro nível, **domínio** e **infraestrutura**, com
o objetivo de isolar as classes de domínio das tecnologias necessárias para
interagir com o mundo exterior.

```
src/main/java/com/pucrs/planosdevoo/
├── PlanosDeVooApplication.java          ponto de entrada do Spring Boot
├── domain/                              NÃO conhece Spring, JPA nem infra
│   ├── model/                           entidades, value objects, enums, serviços de domínio
│   └── ports/
│       ├── in/usecases/                 casos de uso (...UC) e commands (...Command)
│       └── out/repositories/            interfaces dos repositórios (Repositorio...)
└── infra/                               conhece domain; a seta é sempre para dentro
    ├── adapters/
    │   ├── in/rest/                     controllers, DTOs e tradução de erro para HTTP
    │   └── out/persistence/             entidades JPA, Spring Data, Repositorio...JPA, mappers
    └── config/                          UseCaseConfig: cria os casos de uso como beans

src/main/resources/
├── application.properties
└── db/
    ├── schema.sql                       schema, versionado
    └── data.sql                         carga inicial e data de referência

src/test/java/com/pucrs/planosdevoo/     espelha a árvore de main
```

É a mesma organização do estudo de caso da cadeira (`P7a`) e do exemplo
`ex3-sist-vendas-hexagonal`: **Domain** (Model, Ports In UseCases, Ports Out
Repositories) e **Infra** (Adapters In Rest, Adapters Out Persistence, Config).

## O que vai em cada pasta

| Pasta | O que mora aqui | O que NÃO mora aqui |
|---|---|---|
| `domain/model` | `PlanoDeVoo`, `Aerovia`, `Aeroporto`, `Aeronave` e subclasses, `Piloto`, `SituacaoPlano` (e depois `Trecho`, `Pouso`, reservas) | qualquer `import` de Spring ou JPA |
| `domain/ports/in/usecases` | `ConsultarAeroportosUC`, `CriarPlanoUC` + `CriarPlanoCommand`, `ListarPlanosUC`… | regra que pertence à entidade |
| `domain/ports/out/repositories` | `RepositorioAeroportos`, `RepositorioPlanosDeVoo`… (interfaces) | JPA, SQL, `JpaRepository` |
| `infra/adapters/in/rest` | `PlanoDeVooController`, `MalhaAereaController`, `FrotaController`, `...RequestDTO`, `...ResponseDTO`, `RestExceptionHandler` | regra de negócio, consulta ao banco |
| `infra/adapters/out/persistence` | `...JpaEntity`, `...SpringDataRepository`, `Repositorio...JPA`, `...Mapper` | regra de negócio |
| `infra/config` | `UseCaseConfig` | lógica |

## A regra que o PR cobra

`domain/` não importa `infra`, `org.springframework` nem `jakarta.persistence`
— **nem anotação**. Se `PlanoDeVoo` tiver `@Entity`, o isolamento acabou e o
diagrama de componentes vira ficção. A conferência é literal:

```sh
grep -rn "org.springframework\|jakarta.persistence\|infra\." src/main/java/com/pucrs/planosdevoo/domain/
```

Esse comando tem que devolver **nada**.

## Diferença em relação ao exemplo da cadeira

No exemplo do professor, os casos de uso têm `@Component`. Aqui eles não têm
nenhuma anotação: viram beans em `infra/config/UseCaseConfig`. O `P7a` pede "a
maior independência possível" do modelo em relação ao Spring, e assim
`domain/` fica sem nenhum import de framework. Registrado em `logs/decisoes.md`.

## Comandos

| O que | Comando |
|---|---|
| Compilar e testar | `./mvnw clean test` |
| Empacotar | `./mvnw clean package` |
| Executar | `./mvnw spring-boot:run` |
| Um teste só | `./mvnw test -Dtest=SlotTest` |

## Pendências desta pasta

- [x] `schema.sql` com as tabelas de aeroporto, aerovia, piloto, aeronave e
      plano de voo.
- [x] `schema.sql` com as tabelas `trecho`, `reserva_aerovia` e `reserva_vaga`,
      incluindo `UNIQUE (aerovia_id, altitude, slot_inicio)` e
      `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)` (PRs #8 e #10).
- [x] `data.sql` com a malha inicial, pilotos e aeronaves das três categorias.
- [ ] `data.sql`: faltam os planos aprovados com suas reservas, cobrindo pelo
      menos 5 das 8 aerovias na data de referência. **É a última pendência de
      dado do projeto** — sem ela não há ocupação prévia para demonstrar
      conflito de altitude, vaga ocupada e cancelamento recusado.
- [x] Data de referência definida: **19/10/2026**, declarada no `README.md` da raiz.
- [x] Dependência do springdoc (OpenAPI) ativa no `pom.xml`, versão `2.8.6`,
      compatibilidade com o Spring Boot 4.1.1 conferida na prática (PR #9).
- [ ] `groupId` / pacote `com.pucrs` trocado, se a equipe preferir outro.
