# backend — estrutura de pastas

Entrega de **07/10**. A organização segue o estudo de caso de arquitetura
hexagonal da cadeira: no primeiro nível, **domínio** e **infraestrutura**, com
o objetivo de isolar as classes de domínio das tecnologias necessárias para
interagir com o mundo exterior.

```
src/main/java/com/pucrs/planosdevoo/
├── PlanosDeVooApplication.java      ponto de entrada do Spring Boot
├── domain/                          NÃO conhece Spring, JPA nem infra
│   ├── model/                       entidades, value objects, enums, serviços de domínio
│   ├── servicos/                    implementações dos casos de uso (...Service)
│   └── ports/
│       ├── in/                      interfaces dos casos de uso (...UseCase)
│       └── out/                     interfaces dos repositórios (...Repository)
└── infra/                           conhece domain; a seta é sempre para dentro
    ├── adapters/
    │   ├── in/rest/                 controllers (...Controller)
    │   │   ├── dto/                 ...Request e ...Response
    │   │   └── exception/           tradução de erro de domínio para status HTTP
    │   └── out/persistence/
    │       ├── entity/              entidades de banco (...Entity)
    │       ├── repository/          implementação das portas de saída (Jpa...Repository)
    │       └── mapper/              conversão domínio ↔ persistência (...Mapper)
    └── config/                      beans de configuração (...Config)

src/main/resources/
├── application.properties
└── db/
    ├── schema.sql                   schema, versionado
    └── data.sql                     carga inicial e data de referência

src/test/java/com/pucrs/planosdevoo/   espelha a árvore de main
```

## O que vai em cada pasta

| Pasta | O que mora aqui | O que NÃO mora aqui |
|---|---|---|
| `domain/model` | `PlanoDeVoo`, `Trecho`, `Pouso`, `Aerovia`, `Aeroporto`, `Aeronave` e subclasses, `Piloto`, `ReservaAerovia`, `ReservaVaga`, `Slot`, `SituacaoPlano`, `AcaoNoDestino` | qualquer `import` de Spring ou JPA |
| `domain/servicos` | orquestração do caso de uso: `AprovarPlanoService`, `ValidarPlanoService` | regra de negócio que pertence à entidade |
| `domain/ports/in` | `CriarPlanoUseCase`, `AdicionarTrechoUseCase`, `ValidarPlanoUseCase`, `AprovarPlanoUseCase`, `CancelarPlanoUseCase`… | implementação |
| `domain/ports/out` | `PlanoRepository`, `AerovíaRepository`, `ReservaRepository`… (interfaces) | JPA, SQL, `JpaRepository` |
| `infra/adapters/in/rest` | `PlanoController`, `MalhaController`, `AeronaveController` | regra de negócio, consulta ao banco |
| `infra/adapters/out/persistence` | `PlanoEntity`, `JpaPlanoRepository`, `PlanoMapper` | regra de negócio |
| `infra/config` | beans, `@Bean` de ligação entre porta e adaptador | lógica |

## A regra que o PR cobra

`domain/` não importa `infra`, `org.springframework` nem `jakarta.persistence`
— **nem anotação**. Se `PlanoDeVoo` tiver `@Entity`, o isolamento acabou e o
diagrama de componentes vira ficção. A conferência é literal:

```sh
grep -rn "org.springframework\|jakarta.persistence\|infra\." src/main/java/com/pucrs/planosdevoo/domain/
```

Esse comando tem que devolver **nada**.

## Pequeno desvio do material da cadeira

O estudo de caso coloca entidades **e** serviços juntos em `Domain/Model`.
Aqui os serviços que implementam casos de uso foram separados em
`domain/servicos/`, para que `model/` fique só com o modelo. É um refinamento,
não uma mudança de estilo — mas, por ser desvio da referência, está registrado
em `logs/decisoes.md` e deve ser citado na apresentação.

## Comandos

| O que | Comando |
|---|---|
| Compilar e testar | `./mvnw clean test` |
| Empacotar | `./mvnw clean package` |
| Executar | `./mvnw spring-boot:run` |
| Um teste só | `./mvnw test -Dtest=SlotTest` |

## Pendências desta pasta

- [ ] `schema.sql` e `data.sql` escritos (hoje são só comentários).
- [ ] Data de referência definida, no `data.sql` e no `README.md` da raiz.
- [ ] Dependência do springdoc (OpenAPI) descomentada no `pom.xml`, com a
      versão compatível conferida.
- [ ] `groupId` / pacote `com.pucrs` trocado, se a equipe preferir outro.
