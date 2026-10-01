# Guia do repositório `trabalho-1`

Contexto de trabalho para o **Trabalho Final (parte 1) de Projeto e Arquitetura
de Software** — PUCRS, 4º semestre, turma 340, 2026/2, Prof. Bernardo Copstein.

Tema: **Sistema de Gestão de Planos de Voo**. Backend em Java + Spring Boot,
com API REST, banco de dados e documentação de arquitetura.

**Como ler este arquivo**

- A Parte A é o que precisa estar na cabeça antes de escrever qualquer linha:
  o que o trabalho é, qual material serve de base, o que entregar e quando.
- As Partes B a E são de consulta: volta nelas quando for modelar, codar,
  mexer no banco ou preparar a entrega.
- A Parte F tem os checklists e as armadilhas do enunciado.

**Duas regras que valem acima de tudo**

1. **O enunciado vence.** Onde este guia divergir de
   `PAS_TF_2026_2_PlanosDeVoo (1).md`, o enunciado está certo e este guia é
   corrigido.
2. **O que não está registrado não existe.** Toda sessão de trabalho termina
   com entrada nos logs (seção 9). Sem isso não há como montar a apresentação
   nem justificar decisão de arquitetura.

---

## Sumário

**Parte A: começar bem**

1. [O que é este trabalho](#1-o-que-é-este-trabalho)
2. [Material de base: a pasta PROJAQ](#2-material-de-base-a-pasta-projaq)
3. [Cronograma e entregas](#3-cronograma-e-entregas)
4. [Estrutura do repositório](#4-estrutura-do-repositório)

**Parte B: domínio**

5. [Regras de negócio consolidadas](#5-regras-de-negócio-consolidadas)
6. [Modelo de domínio atual](#6-modelo-de-domínio-atual)
7. [Casos de uso](#7-casos-de-uso)

**Parte C: arquitetura e código**

8. [Arquitetura: hexagonal, como o professor ensina](#8-arquitetura-hexagonal-como-o-professor-ensina)
9. [Registro do trabalho: os dois logs](#9-registro-do-trabalho-os-dois-logs)
10. [Git: branch, commit e PR](#10-git-branch-commit-e-pr)
11. [Autorrevisão antes de commitar](#11-autorrevisão-antes-de-commitar)
12. [Padrões de código e Spring Boot](#12-padrões-de-código-e-spring-boot)
13. [Testes](#13-testes)

**Parte D: dados**

14. [Banco de dados e carga inicial](#14-banco-de-dados-e-carga-inicial)

**Parte E: entrega**

15. [Documentação C4 e decisões arquiteturais](#15-documentação-c4-e-decisões-arquiteturais)
16. [README, OpenAPI e apresentação](#16-readme-openapi-e-apresentação)

**Parte F: consulta rápida**

17. [Armadilhas do enunciado](#17-armadilhas-do-enunciado)
18. [Checklists](#18-checklists)

---

# Parte A: começar bem

## 1. O que é este trabalho

Projetar e implementar o **backend** de um sistema que cria, valida e gerencia
planos de voo. Não há frontend. Tudo é exposto por **API REST**.

O que o sistema faz, em uma frase: o usuário monta um plano de voo trecho a
trecho (aerovia, altitude, horário de entrada, o que acontece no aeroporto de
destino do trecho), e o sistema **verifica** se o percurso é contínuo, se os
horários batem com a duração calculada, se a aeronave pode operar naquela
altitude e naquele horário, se a autonomia aguenta, e se altitude e vaga estão
livres — e só então **reserva** tudo de uma vez e aprova.

O que o sistema **não** faz:

- não calcula nem sugere rota (o usuário informa a sequência de aerovias);
- não trata habilitação, licença ou jornada de piloto;
- não tem interface gráfica.

O trabalho é avaliado tanto pela implementação quanto pelo **projeto**: modelo
de domínio em UML, diagramas C4, justificativa das decisões, e coerência entre
documentação e código. Arquitetura mal justificada derruba nota mesmo com o
código funcionando.

Fontes neste repositório:

| Arquivo | O que é |
|---|---|
| `PAS_TF_2026_2_PlanosDeVoo (1).md` | **enunciado completo**, fonte de verdade |
| `PAS_TF_2026_2_CronogramaPlanosDeVoo_P1.pdf` | cronograma das entregas da parte 1 |
| `CLAUDE.md` | este guia |

---

## 2. Material de base: a pasta PROJAQ

Este trabalho é a aplicação do que foi dado em aula. **Antes de inventar
solução, procura no material da cadeira** — o professor avalia contra o que ele
ensinou, e o vocabulário dele (portas, adaptadores, casos de uso, agregados) é
o que deve aparecer no código e nos diagramas.

A pasta fica em `../..`, isto é, `PROJAQ/`, um nível acima de
`Exercícios e Trabalhos/`.

| O que | Onde | Quando consultar |
|---|---|---|
| **Estilo camadas** | `Evolução dos Estilos Arquiteturais/P4_Estilo_Camadas.pdf` | para justificar por que **não** ficou em camadas puras |
| **DDD — conceito** | `Evolução dos Estilos Arquiteturais/P5_DomainDrivenDesign.pdf` | linguagem de domínio, antes de nomear qualquer classe |
| **DDD — blocos de montar** | `Evolução dos Estilos Arquiteturais/P6_DDD_OsBlocosDeMontar.pdf` | entidade, value object, agregado, repositório, serviço de domínio |
| **Hexagonal — estudo de caso** | `Evolução dos Estilos Arquiteturais/P7a_EstudoDeCasoArquiteturaHexagonal.pdf` | **a referência principal**: estrutura de pastas e fluxo da requisição (seção 8) |
| **Princípios de projeto** | `Princípios e Padrões de Projeto/P1_PrincípiosDeProjeto.pdf` | coesão, acoplamento, ocultamento de informação, integridade conceitual |
| **Padrões GoF** | `Princípios e Padrões de Projeto/P2..P5*.pdf` | Strategy para regra de aeronave, Factory para categorias, Builder para plano |
| **Spring Boot — endpoints** | `Introdução ao Spring-Boot/P2_*, P3_*` | `@RestController`, rotas, path param, query string, body |
| **Injeção de dependência / IoC** | `Introdução ao Spring-Boot/P4_*` | beans, `@Component` / `@Service` / `@Repository`, `@Qualifier` |
| **JDBC** | `Introdução ao Spring-Boot/P5a_*` | alternativa ao ORM, se for a decisão da equipe |
| **JPA / ORM** | `Introdução ao Spring-Boot/P5b_*, P6_*` | `@Entity`, `@Id`, `JpaRepository`, relacionamentos 1-n |
| **Autenticação básica** | `Introdução ao Spring-Boot/P7_*` | só se a equipe decidir proteger a API (não é exigido) |
| **Código de exemplo** | `Exercícios e Trabalhos/SemanaDia31-08/ex1-biblioteca-v3-rotasComParametros/` | projeto Spring Boot que roda: `pom.xml`, `Controller`, `testRequests.http` |
| **Exercícios de padrões** | `Exercícios e Trabalhos/TrabEmAula/trabalhoProjArq/ex0-desafioPadroesDeProjeto/` | código próprio com Strategy, Chain, Factory, Adapter, Decorator |
| **Resumos e mapa mental** | `PROJAQ/Resumo Master - PROJAQ.pdf`, `PROJAQ/Mapa Mental - PROJAQ.md` | revisão rápida de tudo |

**Regras de uso do material**

- O `pom.xml` do exemplo da biblioteca é a base de partida: Spring Boot `4.1.1`,
  Java `25`, `spring-boot-starter-web`. Confirma as versões nele antes de criar
  o projeto novo, em vez de chutar.
- O arquivo `testRequests.http` do exemplo mostra o formato de requisição que o
  professor usa em aula. Vale manter um equivalente aqui (seção 16).
- Padrão de projeto só entra se resolver um problema real deste domínio. Padrão
  colocado para aparecer no relatório é acoplamento a troco de nada, e o
  professor pergunta "por quê?" na apresentação.

---

## 3. Cronograma e entregas

Da `PAS_TF_2026_2_CronogramaPlanosDeVoo_P1.pdf`:

| Data | Entrega |
|---|---|
| **05/10** | Diagrama de domínio · Casos de uso identificados |
| **07/10** | Diagramas C4 (Contexto, Containers, Componentes) · Estrutura de pastas |
| 12/10 | FERIADO |
| **14/10** | Implementação de **40% dos casos de uso** |
| **19/10** | **Apresentação da solução completa** |
| 21/10 | SEMANA ACADÊMICA |

O que isso impõe na ordem de trabalho:

1. **Domínio primeiro.** O diagrama de 05/10 é o contrato com o professor. O
   código depois tem que bater com ele — se mudar, o diagrama muda junto e a
   mudança é registrada (seção 9).
2. **Estrutura de pastas antes do código.** A entrega de 07/10 é a árvore de
   pacotes vazia (ou quase), já no formato da seção 8. Criar pasta depois,
   conforme dá vontade, é como a arquitetura documentada deixa de bater com a
   implementada.
3. **40% em 14/10** quer dizer escolher quais casos de uso. Escolhe os que
   demonstram o **ciclo completo** de um plano (criar → adicionar trecho →
   validar → aprovar), não os cinco CRUDs mais fáceis.
4. **19/10 é demonstração ao vivo**, com requisições que o professor pode
   pedir na hora. Dado inicial e data de referência precisam estar prontos e
   reproduzíveis muito antes disso.

---

## 4. Estrutura do repositório

O repositório já está inicializado (`git init`, branch `main`), com a
documentação, os logs e os padrões no lugar. O que falta é `backend/`.

Estado atual e alvo — `(a criar)` marca o que ainda não existe:

```
trabalho-1/
├── CLAUDE.md                      este guia
├── README.md                      esqueleto, com [PREENCHER] nas seções exigidas
├── .gitignore                     Java, Maven, banco local, IDE, macOS
├── PAS_TF_2026_2_PlanosDeVoo (1).md
├── PAS_TF_2026_2_CronogramaPlanosDeVoo_P1.pdf
├── .github/
│   └── pull_request_template.md   template de PR (seção 10.4)
├── docs/
│   ├── padroes-desenvolvimento.md branch, commit, PR, nomenclatura Java
│   ├── arquitetura/README.md      o que cada nível do C4 mostra
│   ├── dominio/
│   │   ├── README.md
│   │   ├── modelo-dominio.puml        (a criar) fonte do diagrama, PlantUML
│   │   ├── modelo-dominio.png         (a criar) imagem exportada, datada
│   │   └── casos-de-uso.md            (a criar) UC01–UC19 detalhados (seção 7)
│   ├── arquitetura/
│   │   ├── c4-contexto.puml / .png    (a criar)
│   │   ├── c4-containers.puml / .png  (a criar)
│   │   ├── c4-componentes.puml / .png (a criar)
│   │   └── decisoes/                  um ADR por decisão (seção 15)
│   │       ├── README.md              índice das 8 decisões exigidas
│   │       └── 0000-template.md       formato do ADR
│   └── api/
│       ├── README.md
│       └── requests.http              (a criar) roteiro da apresentação
├── logs/
│   ├── log.md                         o que foi feito (seção 9)
│   └── decisoes.md                    o que foi decidido e por quê (seção 9)
└── backend/                           projeto Spring Boot (seção 8) — compila
    ├── pom.xml · mvnw · .mvn/
    ├── README.md                      o que vai em cada pasta
    ├── src/main/java/com/pucrs/planosdevoo/
    │   ├── PlanosDeVooApplication.java
    │   ├── domain/{model, servicos, ports/in, ports/out}
    │   └── infra/{adapters/in/rest, adapters/out/persistence, config}
    ├── src/main/resources/
    │   ├── application.properties     H2 em arquivo, não em memória
    │   └── db/{schema.sql, data.sql}  (a escrever)
    └── src/test/java/...              espelha a árvore de main
```

**`CLAUDE.md` × `CONTEXT_NEW.md`:** este guia cita o material local da cadeira
por caminho (seção 2) e serve a quem tem a pasta PROJAQ baixada. O
`CONTEXT_NEW.md` é autocontido — explica o conteúdo da cadeira em vez de apontar
para os PDFs — e é o que se compartilha com a equipe e com outras ferramentas de
IA. **As duas precisam ser atualizadas juntas** quando uma regra do projeto
mudar.

Regras da pasta:

- `docs/` é entregável e versionado. Diagrama exportado tem a data no nome ou
  no rodapé da imagem.
- `logs/` é versionado também: ele é a evidência do processo e a matéria-prima
  da apresentação.
- Nada de `.class`, `target/`, `.DS_Store` ou `.idea/` no Git.
- Arquivo solto fora do repositório **não é entrega**.

---

# Parte B: domínio

## 5. Regras de negócio consolidadas

Esta seção é o resumo operacional do enunciado. Serve para consulta rápida —
mas quando houver dúvida, abre o enunciado.

### 5.1 Malha aérea inicial

Aeroportos:

| Código | Aeroporto | Vagas |
|---|---|---|
| POA | Salgado Filho — Porto Alegre | 41 |
| FLN | Hercílio Luz — Florianópolis | 16 |
| CWB | Afonso Pena — Curitiba | 26 |
| GRU | Guarulhos — São Paulo | 115 |

Aerovias (**orientadas**: A→B não implica B→A):

| Aerovia | Origem | Destino | Distância (km) |
|---|---|---|---|
| R1 | POA | FLN | 363 |
| R2 | POA | GRU | 865 |
| R3 | GRU | POA | 865 |
| R4 | FLN | CWB | 245 |
| R5 | CWB | FLN | 245 |
| R6 | CWB | GRU | 360 |
| R7 | GRU | CWB | 360 |
| R8 | FLN | POA | 363 |

**Aerovia é imutável após o cadastro.** Não altera, não exclui. Só cria nova,
com id novo. Isso preserva os dados referenciados por planos e reservas — e é
uma restrição de projeto, não só de API: nada de `PUT /aerovias/{id}` nem de
setter público em `Aerovia`.

Novos aeroportos e aerovias podem ser informados pelo professor e **têm que
entrar sem alterar a lógica da aplicação**. Ou seja: malha vem do banco, nunca
de constante no código.

### 5.2 Aeronaves

Toda aeronave: prefixo único, velocidade de cruzeiro (km/h), autonomia (km).

| Categoria | Dados próprios | Altitudes permitidas | Janela de horário |
|---|---|---|---|
| Particular | empresa de manutenção | só 25.000, 26.000, 27.000 pés | qualquer |
| Comercial de passageiros | companhia aérea, capacidade de passageiros | ≥ 28.000 pés, dentro da faixa | qualquer |
| Comercial de carga | companhia aérea, capacidade em toneladas | qualquer altitude prevista | **todo** o uso da aerovia entre 00:00 (inclusive) e 06:00 (exclusive) |

**Novas categorias e novas regras podem aparecer durante o desenvolvimento.**
Esse é o ponto de extensão que o professor vai testar. A regra "pode operar?"
precisa estar isolada — uma operação polimórfica na hierarquia de aeronave, ou
uma Strategy por categoria. `if (aeronave instanceof AeronaveCarga)` espalhado
pelo serviço é exatamente o que o trabalho está cobrando que não se faça.

### 5.3 Autonomia

- Margem de segurança de 15%: a distância desde o último abastecimento não pode
  passar de **85% da autonomia nominal**.
- **Sobrevoo acumula** distância.
- **Pouso reabastece**: zera o acumulado antes de seguir.

### 5.4 Horários

```
duração do trecho = distância da aerovia / velocidade de cruzeiro da aeronave
horário de chegada = horário de entrada + duração
```

| Situação | Horário de entrada na aerovia seguinte |
|---|---|
| Primeiro trecho | = partida do plano |
| Depois de sobrevoo | = chegada calculada |
| Depois de pouso intermediário | = chegada calculada + tempo de permanência |

O horário de chegada é **calculado pelo sistema**, não informado. Horário
informado que não bate com o cálculo é erro de validação, e é um dos casos de
falha que precisam ser demonstrados.

### 5.5 Percurso

- Sequência ordenada de trechos. Cada trecho: aerovia, altitude, horário de
  entrada, ação no destino (SOBREVOO ou POUSO).
- Origem da 1ª aerovia = origem do plano; destino de cada uma = origem da
  seguinte; destino da última = destino do plano.
- **No destino final o pouso é obrigatório.**
- Todo pouso informa vaga pretendida e permanência em minutos — inclusive o
  final. Indicar vaga **não é reservar**.
- **Voo inicia e termina no mesmo dia** (chegada ao destino final na data da
  partida). A permanência no destino final **pode** passar da meia-noite, e a
  reserva da vaga cobre todos os slots necessários.

### 5.6 Slots e ocupação

Altitudes: de **25.000 a 35.000 pés**, inclusive, de 1.000 em 1.000 — 11
altitudes.

Ocupação é controlada em **slots de 1 hora alinhados à hora cheia**. Qualquer
uso parcial ocupa o slot inteiro.

| Exemplo | Slots ocupados |
|---|---|
| Aerovia: entrada 15:52, saída 17:07 | 15:00–16:00, 16:00–17:00, 17:00–18:00 |
| Vaga: chegada 15:50, permanência 20 min | 15:00–16:00, 16:00–17:00 |

Conflito = coincidência em **pelo menos um** slot, na mesma altitude da mesma
aerovia, ou na mesma vaga do mesmo aeroporto.

O cálculo de slots é a regra mais fácil de errar por um slot a mais ou a menos.
Isola numa operação própria (um value object `Slot` com algo como
`Slot.entre(inicio, fim)`) e cobre com teste unitário antes de ligar no resto.

### 5.7 Estados do plano

```
                 aprovar (tudo validado + tudo reservado)
    EM_EDICAO ──────────────────────────────────────────► APROVADO
        │                                                     │
        │ abandonar                       cancelar (só se partida ainda não ocorreu;
        ▼                                  libera todas as reservas)
    ABANDONADO                                                ▼
                                                          CANCELADO
```

| Regra | Detalhe |
|---|---|
| Nasce | EM_EDICAO |
| Só EM_EDICAO pode ser modificado | aprovado/cancelado/abandonado preservam o que tinham |
| EM_EDICAO não reserva nada | nem ao validar |
| Aprovação é **integral** | ou reserva tudo e vira APROVADO, ou não deixa reserva nenhuma e continua EM_EDICAO |
| Abandono | só de EM_EDICAO; não há reserva a liberar |
| Cancelamento | só de APROVADO **com partida ainda no futuro**; libera todas as reservas |
| Partida já ocorrida | voo executado: consultável, **não cancelável** |
| Consulta | por id e por situação; cancelado e abandonado continuam consultáveis |

**Validar ≠ aprovar.** Validar não muda estado nem reserva. A aprovação
reverifica (ou garante que continua válido) no momento das reservas — a
disponibilidade consultada antes pode ter mudado.

---

## 6. Modelo de domínio atual

Versão já desenhada (ver `docs/dominio/`). Serve de ponto de partida; muda com
justificativa registrada em `logs/decisoes.md`.

| Elemento | Tipo | Papel |
|---|---|---|
| `PlanoDeVoo` | entidade / **raiz de agregado** | id, partida, situação; `adicionarTrecho`, `removerTrecho`, `validar() : List<String>`, `aprovar`, `abandonar`, `cancelar(agora)` |
| `Trecho` | entidade | ordem, altitude, entrada, ação; `chegada()`, `slotsOcupados()` |
| `Pouso` | **value object** (0..1 no trecho) | numeroVaga, permanenciaMin; `saida(chegada)` |
| `Aerovia` | entidade | id, distanciaKm, origem, destino; `duracao(velocidade) : Duration`; **imutável após cadastro** |
| `Aeroporto` | entidade | codigo, nome, quantidadeVagas; `possuiVaga(numero)` |
| `Aeronave` | **abstrata** | prefixo, velocidadeCruzeiroKmh, autonomiaKm; `distanciaMaxSemAbastecer()`, `podeOperar(altitude, entrada, saida) : boolean` |
| `AeronaveParticular` | concreta | empresaManutencao |
| `AeronaveComercial` | abstrata | companhiaAerea |
| `AeronaveCarga` | concreta | capacidadeToneladas |
| `AeronavePassageiros` | concreta | capacidadePassageiros |
| `Piloto` | entidade | id, nome |
| `ReservaAerovia` | entidade | altitude, slot |
| `ReservaVaga` | entidade | numeroVaga, slot |
| `Slot` | **value object** | inicio; `entre(inicio, fim) : List<Slot>` |
| `SituacaoPlano` | enum | EM_EDICAO, APROVADO, CANCELADO, ABANDONADO |
| `AcaoNoDestino` | enum | SOBREVOO, POUSO |

**Decisões que esse desenho já embute** — e que precisam estar escritas como
ADR, porque o professor vai perguntar:

1. `podeOperar` é **polimórfico na aeronave**, não um `if` por categoria no
   serviço. É assim que categoria nova entra sem mexer na validação.
2. `Slot` e `Pouso` são **value objects**: não têm identidade, são comparados
   por valor. Vem direto dos blocos de montar do DDD (`P6`).
3. `PlanoDeVoo` é **raiz de agregado**: `Trecho` só existe dentro dele, e toda
   alteração passa pelo plano. É o que garante a consistência da transação de
   aprovação.
4. `validar()` devolve **lista de problemas**, não `boolean` nem exceção. A API
   precisa dizer *o que* está errado, e o enunciado cobra demonstração de vários
   tipos de falha.
5. Reserva é **entidade separada** do trecho, e não um campo dentro dele. É o
   que permite aprovar tudo de uma vez e liberar tudo de uma vez.

Pontos ainda em aberto para decidir e registrar:

- `Slot` guarda a aerovia/aeroporto ou isso fica na reserva?
- A unicidade "mesma altitude + mesma aerovia + mesmo slot" é garantida por
  `UNIQUE` no banco, por lógica no domínio, ou pelos dois?
- O controle de concorrência na aprovação (duas aprovações simultâneas na
  mesma altitude) é transação + constraint, ou lock otimista?

---

## 7. Casos de uso

Entrega de 05/10. Numerados, com ator, pré-condição e resultado. Esta é a base
para escolher os 40% de 14/10.

| # | Caso de uso | Observação |
|---|---|---|
| UC01 | Consultar aeroportos | |
| UC02 | Cadastrar aeroporto | |
| UC03 | Consultar aerovias | |
| UC04 | Cadastrar aerovia | sem alteração e sem exclusão |
| UC05 | Consultar pilotos | |
| UC06 | Cadastrar piloto | |
| UC07 | Consultar aeronaves | |
| UC08 | Cadastrar aeronave | por categoria |
| UC09 | Criar plano de voo | nasce EM_EDICAO |
| UC10 | Adicionar trecho ao percurso | só EM_EDICAO |
| UC11 | Remover / alterar trecho | só EM_EDICAO |
| UC12 | Consultar altitudes disponíveis em uma aerovia por período | não reserva |
| UC13 | Consultar vagas disponíveis em um aeroporto por período | não reserva |
| UC14 | **Validar plano** | não muda estado, não reserva; devolve lista de problemas |
| UC15 | **Aprovar plano** | integral: tudo ou nada |
| UC16 | Consultar plano por id | |
| UC17 | Consultar planos por situação | |
| UC18 | Abandonar plano | só EM_EDICAO |
| UC19 | **Cancelar plano** | só APROVADO com partida futura; libera reservas |

Os 40% de 14/10 devem cobrir o fio condutor: **UC09, UC10, UC14, UC15**, mais o
mínimo de consulta de malha (UC01, UC03) que eles exigem. Com isso dá para
demonstrar o ciclo inteiro; sem isso, dez CRUDs não demonstram nada.

---

# Parte C: arquitetura e código

## 8. Arquitetura: hexagonal, como o professor ensina

O estilo é decisão da equipe e precisa ser **justificado** — mas a referência
mais forte do material é o `P7a_EstudoDeCasoArquiteturaHexagonal.pdf`, que usa
a loja online modelada em DDD e mostra exatamente a organização esperada.

Objetivo do estilo, nas palavras do material: **isolar as classes de domínio
das tecnologias** necessárias para interagir com o mundo exterior.

Primeiro nível: **Domínio** e **Infraestrutura**.

```
<raiz>
├── domain/
│   ├── model/              entidades, value objects e serviços de domínio
│   └── ports/
│       ├── in/             casos de uso (portas de entrada)
│       └── out/            interfaces dos repositórios (portas de saída)
└── infra/
    ├── adapters/
    │   ├── in/rest/        controllers, DTOs de entrada e saída
    │   └── out/persistence/ entidades de BD e implementação dos repositórios
    └── config/             beans de configuração
```

Fluxo de uma requisição (os cinco passos do material):

| # | Elemento | Responsabilidade |
|---|---|---|
| 01 | Adaptador de entrada (controller) | fala HTTP e a linguagem do mundo externo (DTOs); aciona a porta de entrada |
| 02 | Porta de entrada (caso de uso) | orquestra a lógica da aplicação usando os elementos do modelo |
| 03 | Modelo | abstrações que representam entidades e capacidades de negócio |
| 04 | Portas de saída | abstraem o acesso externo; são usadas pelos elementos do modelo |
| 05 | Adaptadores de saída | implementam as abstrações definidas pelas portas de saída |

**A regra de dependência**, que é o que de fato se avalia:

- `domain` **não importa nada** de `infra`, de Spring Web, de JPA, de
  `jakarta.persistence`. Nem anotação. Se `PlanoDeVoo` tem `@Entity`, o
  isolamento acabou e o diagrama vira ficção.
- `infra` conhece `domain`. O sentido da seta é sempre para dentro.
- A entidade de persistência é **outra classe** da entidade de domínio, com um
  mapeador entre as duas. Isso custa código extra, e é justamente a decisão que
  o ADR precisa defender — com o ganho (trocar JPA por JDBC sem tocar no
  domínio) e o custo (duplicação de estrutura) escritos.

Se a equipe decidir **não** separar as duas (usar a entidade JPA como entidade
de domínio), tudo bem — mas aí é uma arquitetura em camadas com influência
hexagonal, e tem que ser documentada e chamada assim. Arquitetura documentada
diferente da implementada é critério de avaliação explícito no enunciado.

---

## 9. Registro do trabalho: os dois logs

A prática que sustenta a apresentação e a documentação de decisões. **Ao fim de
toda sessão de trabalho**, escreve nos dois arquivos de `logs/`:

| | `logs/log.md` | `logs/decisoes.md` |
|---|---|---|
| Registra | o que foi feito | o que foi decidido e por quê |
| Serve para | apresentação, README, relato de progresso | justificar arquitetura, retomar de onde parou |
| Entra | commit, arquivo alterado, caso de uso concluído, verificação feita | decisão, alternativa descartada, achado, dúvida em aberto |
| Não entra | intenção, plano | entrega |

### 9.1 Regras

- **Sempre acrescentar.** Nunca reescrever nem apagar entrada antiga. Entrada
  errada é corrigida por uma nova, apontando a correção.
- **Fidelidade.** Só o que foi de fato feito. Código não commitado entra como
  pendência, não como entrega.
- **Sessão sem commit também conta.** Leitura do enunciado, desenho do
  diagrama, decisão de modelagem: registra com "Commits: nenhum".
- Decisão só entra em "Decisões" se foi **fechada**. Ideia ainda em discussão
  vai para "Em aberto".
- Toda mudança no modelo de domínio ou na estrutura de pastas gera entrada em
  `decisoes.md`, porque muda um artefato já entregue ao professor.

### 9.2 Formato do `log.md`

```markdown
## AAAA-MM-DD — título curto

**Resumo.** O que foi feito, em 2 a 5 linhas.

**Casos de uso tocados.** UC14 (validação de percurso) — implementado e testado.

**Arquivos tocados.**
- `backend/src/.../PlanoDeVoo.java` — o que mudou

**Verificações feitas.**
- `./mvnw test` — N testes, todos passando
- requisições manuais em `docs/api/requests.http`: quais e o resultado
- o que NÃO foi testado

**Commits.**
- `abc1234` — mensagem do commit

**Pendências.**
- O que ficou aberto e o próximo passo
```

### 9.3 Formato do `decisoes.md`

```markdown
## AAAA-MM-DD — título curto

**Assunto.** Do que se tratou, em 1 ou 2 linhas.

**Decisões.**
- O que ficou decidido e por quê. Alternativa descartada e o motivo.

**Em aberto.**
- Pergunta sem resposta, e do que ela depende.

**Achados técnicos.**
- Fato verificado no enunciado, no material da cadeira ou no código, com a
  referência (arquivo e seção).

**Retomar por aqui.**
- O próximo passo, escrito para ser lido dias depois sem contexto.
```

O campo mais valioso é **"Decisões" com a alternativa descartada**. É ele que
responde o professor quando pergunta "por que não fez do outro jeito?", e é ele
que vira o ADR de `docs/arquitetura/decisoes/`.

### 9.4 Início e fim de sessão

No início: ler o "Retomar por aqui" da última entrada de `decisoes.md`, depois
conferir o estado real antes de planejar:

```sh
git -C backend status
```

```sh
git log --oneline -5
```

No fim: conferir mudança sem commit, escrever as duas entradas, anotar o
próximo passo.

---

## 10. Git: branch, commit e PR

**A referência completa é `docs/padroes-desenvolvimento.md`.** Esta seção é o
resumo operacional; onde as duas divergirem, o arquivo de padrões vence.

Os padrões vêm dos usados no projeto de AGES II, adaptados para Java e para
este trabalho: **sem ID de task** (não há ferramenta de tasks aqui — o rastro é
o caso de uso e a entrega do cronograma) e com os tipos `UI` e `Deploy`
substituídos por `Database` e `Test`.

Branch de integração: **`main`**.

### 10.1 Formatos

| Elemento | Formato | Exemplo |
|---|---|---|
| Branch | `<tipo>/<descricao>` em kebab-case, inglês, sem ID | `feat/flight-plan-validation` |
| Commit | `<tipo>: <descricao>`, inglês, imperativo, minúsculo, sem ponto | `feat: add flight plan approval use case` |
| Título do PR | `[TIPO] Descrição objetiva`, em português | `[FEAT] Adiciona aprovação de plano de voo` |

### 10.2 Tipos

| Tipo | Branch / Commit | PR | Quando usar |
|---|---|---|---|
| Feature | `feat` | `[FEAT]` | caso de uso ou funcionalidade nova |
| Bugfix | `fix` | `[FIX]` | correção de defeito |
| Refactoring | `refactor` | `[REFACTOR]` | muda estrutura sem mudar comportamento |
| Database | `db` | `[DB]` | schema, migration, carga inicial, índice, constraint |
| Documentation | `docs` | `[DOCS]` | diagrama, ADR, README, este guia |
| Test | `test` | `[TEST]` | só testes |
| Chore | `chore` | `[CHORE]` | build, dependência, configuração |
| Infrastructure | `infra` | `[INFRA]` | Docker, CI, ambiente de execução |

Um commit, uma intenção. Se a mensagem precisa de "and" duas vezes, são dois
commits.

### 10.3 Fluxo de uma mudança

```sh
git switch main && git pull
```

```sh
git switch -c feat/flight-plan-validation
```

Commits pequenos → autorrevisão (seção 11) → `./mvnw clean test` →
atualizar com `main` → abrir o PR.

```sh
git switch main && git pull && git switch - && git merge main --no-edit
```

```sh
git push -u origin feat/flight-plan-validation
```

### 10.4 Pull request

O template está em `.github/pull_request_template.md` e o GitHub o carrega
sozinho. Ele cobra, além dos padrões de código:

- a **regra de dependência** da hexagonal (`domain` sem Spring, sem JPA, sem
  `infra`);
- o estado do **banco e da carga inicial** (as 8 aerovias, as 5 com ocupação na
  data de referência, persistência após reinício);
- **teste de domínio rodando sem Spring e sem banco**;
- diagrama e ADR atualizados quando a mudança mexeu no domínio ou na estrutura;
- as entradas feitas nos dois logs.

Regras do texto do PR:

- **Começa pelo problema**, não pela solução.
- **Antecipa a pergunta do revisor** na seção de observações.
- **Diz a limitação** e o que **não** foi testado.
- Checklist marcado com verdade. Item marcado sem ter sido feito tira o valor
  do checklist inteiro.

### 10.5 Histórico como evidência

O histórico do Git é parte do que mostra o processo. Commit único e gigante no
dia 18/10 conta uma história ruim, mesmo com o código certo. Commita à medida
que cada caso de uso fecha.

---

## 11. Autorrevisão antes de commitar

Terminar de escrever código não é terminar. Antes de cada commit, passa por
esta lista **por escrito**, anotando o resultado:

| # | Item | Como conferir |
|---|---|---|
| 1 | **Regra de dependência** | nenhum import de `infra`, Spring ou JPA dentro de `domain` |
| 2 | **Bate com o diagrama** | classe, atributo e operação novos estão no modelo de domínio entregue? se não, atualiza o diagrama |
| 3 | **Padrão do projeto** | nomes, pacotes e idioma iguais aos dos arquivos vizinhos |
| 4 | **Reúso** | já existe método, value object ou constante que faz isso? |
| 5 | **Sem sobra** | cada classe, método e constante nova tem quem a use |
| 6 | **Sem checagem repetida** | a mesma regra validada no controller e no domínio? uma delas sobra |
| 7 | **Sem regra no controller** | controller só traduz HTTP; regra fica no domínio ou no caso de uso |
| 8 | **Sem `instanceof` de categoria** | regra por categoria de aeronave é polimorfismo, não `if` |
| 9 | **Comentários** | só o porquê, na densidade dos arquivos vizinhos |
| 10 | **Build e testes** | `./mvnw clean test` rodado **depois** da última alteração |
| 11 | **Decisões discutíveis** | lista à parte, que vai para `decisoes.md` |

**Método, não memória.** Para cada nome novo, roda a busca e anota quem usa:

```sh
grep -rn "nomeDaClasse" backend/src/
```

E relê o arquivo inteiro, não só o trecho alterado.

---

## 12. Padrões de código e Spring Boot

### 12.1 Nomenclatura

**A tabela completa está em `docs/padroes-desenvolvimento.md`, seção 2.** O
essencial:

| Elemento | Convenção | Exemplo |
|---|---|---|
| Classe / interface / record | `PascalCase` | `PlanoDeVoo`, `AprovarPlanoUseCase` |
| Arquivo | `PascalCase.java`, nome da classe pública | `PlanoDeVoo.java` |
| Método | `camelCase`, começa com verbo | `adicionarTrecho`, `slotsOcupados` |
| Booleano | prefixo `is`/`has`/`can`/`should` ou `pode`/`possui` | `podeOperar`, `possuiVaga` |
| Variável e parâmetro | `camelCase` | `velocidadeCruzeiroKmh` |
| Constante | `UPPER_SNAKE_CASE` | `MARGEM_SEGURANCA` |
| Pacote | minúsculas, sem `_` | `com.<grupo>.planosdevoo.domain.model` |
| Enum | `PascalCase`, valores `UPPER_SNAKE_CASE` | `SituacaoPlano.EM_EDICAO` |
| Classe de teste | `<Classe>Test` | `SlotTest.java` |

**Sufixos da hexagonal** — o sufixo diz em que pasta a classe mora:
`...UseCase` (porta de entrada), `...Repository` (porta de saída),
`Jpa...Repository` (adaptador de saída), `...Entity` e `...Mapper`
(persistência), `...Controller` (adaptador de entrada), `...Request` /
`...Response` (DTOs), `...Config` (configuração).

**Idioma:** domínio em **português**, infraestrutura em **inglês**. É uma
divergência consciente do padrão de AGES II (que manda tudo em inglês), porque
`PlanoDeVoo`, `Aerovia` e `Trecho` são a linguagem ubíqua do DDD e o vocabulário
do enunciado — traduzir quebraria a correspondência entre enunciado, diagrama e
código, que é o que a avaliação cobra. Branch e commit continuam em inglês.
Registrado em `logs/decisoes.md` e a virar o ADR 0008.

### 12.2 O que o material da cadeira pede

| Tema | Regra |
|---|---|
| Injeção de dependência | por **construtor**, não por campo. É o que permite testar o domínio sem subir o Spring |
| Beans | `@Service` para caso de uso, `@Repository` para adaptador de saída, `@RestController` para adaptador de entrada |
| Resolução de bean | se houver mais de uma implementação da mesma porta, `@Qualifier` ou `@Primary` explícito |
| Rotas | recurso no plural, verbo HTTP correto; path param para identidade, query string para filtro |
| Status HTTP | 201 em criação, 200 em consulta, 404 em recurso inexistente, 409 em conflito de reserva, 422 ou 400 em validação reprovada |
| Corpo de erro | mensagem útil, com a **lista** de problemas da validação — não string solta |
| DTO | entrada e saída da API são DTOs, nunca a entidade de domínio serializada direta |
| Persistência | JPA (`P5b`, `P6`) ou JDBC (`P5a`); a escolha vira ADR |
| Transação | a aprovação é **uma** transação: ou grava todas as reservas, ou nenhuma |

### 12.3 Onde os padrões GoF cabem neste domínio

Não força. Mas estes aparecem naturalmente:

| Padrão | Onde | Por quê |
|---|---|---|
| **Strategy** / polimorfismo | regra de operação por categoria de aeronave | categoria nova sem mexer na validação |
| **Factory** | criação de aeronave a partir do DTO por categoria | esconde as subclasses do controller |
| **Builder** | montagem progressiva do plano de voo | objeto complexo, construído em etapas |
| **Composite** | só se o percurso virar estrutura aninhada | provavelmente não cabe aqui |
| **Singleton** | **evitar** — o material é explícito: esconde dependências e dificulta teste |

---

## 13. Testes

Verificar o comportamento é critério de avaliação explícito.

### 13.1 Prioridade

A regra mais sujeita a erro e mais barata de testar vem primeiro:

1. **Cálculo de slots** — entrada 15:52 / saída 17:07 → 3 slots. Borda exata:
   entrada 15:00 / saída 16:00 → quantos slots? Decide, documenta, testa.
2. **Duração e chegada** do trecho.
3. **Autonomia** com sobrevoo acumulando e pouso zerando.
4. **Regras de altitude e janela** por categoria de aeronave.
5. **Continuidade do percurso** e pouso obrigatório no destino.
6. **Mesmo dia** (chegada na data da partida).

Essas são todas testáveis **sem Spring e sem banco** — e é exatamente isso que
o isolamento do domínio compra. Se um teste de regra de negócio precisa subir a
aplicação, a arquitetura não está isolando nada.

### 13.2 Teste de caso de uso e de API

- Aprovação bem-sucedida: estado vira APROVADO e as reservas existem.
- **Aprovação malsucedida: nenhuma reserva parcial permanece.** Esse é o teste
  que o enunciado cobra por escrito.
- Validação não cria reserva nenhuma.
- Cancelamento de plano futuro libera as reservas; de plano com partida
  passada, é recusado.
- Plano aprovado/cancelado/abandonado recusa modificação.

### 13.3 Antes de qualquer commit

```sh
cd backend && ./mvnw clean test
```

E, para o que não dá para automatizar a tempo, roteiro manual escrito em
`docs/api/requests.http`, com o resultado anotado no `log.md`.

---

# Parte D: dados

## 14. Banco de dados e carga inicial

### 14.1 Exigências do enunciado

- Persistência **em banco de dados**. Memória ou CSV não atende.
- Precisa persistir: malha aérea, pilotos, aeronaves, planos e reservas.
- Alterações feitas pela API **sobrevivem à reinicialização**.
- A carga inicial é **reproduzível a partir dos artefatos entregues** — nada de
  inserir à mão antes da apresentação.

A tecnologia é decisão da equipe. H2 em arquivo é o caminho mais curto a partir
do material da cadeira (`P5b`, `P6`); PostgreSQL em container é mais próximo do
real e exige `docker-compose.yml` no repositório. Qualquer das duas vira ADR —
com a ressalva de que **H2 em memória não atende**, porque o estado se perde ao
reiniciar.

### 14.2 A carga inicial

Precisa existir, já no primeiro start:

- a malha aérea inicial (4 aeroportos, 8 aerovias);
- pilotos;
- aeronaves **das três categorias**;
- um conjunto de **planos aprovados com suas reservas**.

E a restrição numérica que é fácil de esquecer:

> Para uma **data de referência**, pelo menos **60% das aerovias da malha
> inicial** têm alguma ocupação. Com 8 aerovias, **pelo menos 5**.

Mais três condições:

1. As ocupações são **consequência dos planos aprovados** na base — não linhas
   soltas na tabela de reservas. Se você inserir reserva sem plano, o dado
   mente e a demonstração quebra na primeira consulta cruzada.
2. A data de referência fica **claramente identificada** no README. É ela que o
   professor usa nos testes durante a apresentação.
3. Os dados permitem **tanto sucesso quanto conflito**: não pode deixar todos
   os recursos indisponíveis o dia inteiro, senão não dá para demonstrar
   aprovação bem-sucedida.

### 14.3 Checklist de modelagem do banco

| # | Item | O que procurar |
|---|---|---|
| 1 | Tipo correto | data/hora como `TIMESTAMP`, nunca texto; distância e velocidade como numérico |
| 2 | Chave primária | toda tabela tem; tipo consistente |
| 3 | Integridade referencial | FK com `ON DELETE` pensado — lembrando que **aerovia nunca é excluída** |
| 4 | Unicidade | `UNIQUE` no prefixo da aeronave, no código do aeroporto, no id da aerovia |
| 5 | **Unicidade da reserva** | `UNIQUE (aerovia, altitude, slot)` e `UNIQUE (aeroporto, vaga, slot)` — é a última linha de defesa contra duas aprovações simultâneas |
| 6 | Índice | FK usada em filtro precisa de índice; consulta de disponibilidade por período é a mais quente |
| 7 | Herança de aeronave | decidir a estratégia (`SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`) e justificar |
| 8 | Enum | gravado como `STRING`, nunca como ordinal — ordinal quebra ao reordenar o enum |
| 9 | Script de carga | roda do zero, em banco vazio, sem erro, quantas vezes for preciso |

### 14.4 Roteiro de teste da carga inicial

1. Apagar o banco por completo.
2. Subir a aplicação.
3. Conferir: 4 aeroportos, 8 aerovias, pilotos, aeronaves das 3 categorias.
4. Consultar disponibilidade na data de referência em cada uma das 8 aerovias e
   contar quantas têm ocupação — tem que dar **5 ou mais**.
5. Criar, validar e aprovar um plano pela API.
6. **Reiniciar a aplicação** e confirmar que o plano continua lá.

O resultado desse roteiro vai para o `log.md`. É também o roteiro da
demonstração de 19/10.

---

# Parte E: entrega

## 15. Documentação C4 e decisões arquiteturais

### 15.1 Os três níveis exigidos

| Nível | O que mostra | Cuidado |
|---|---|---|
| **1 — Contexto** | o sistema, os usuários e os sistemas externos | aqui o cliente REST aparece; ele não é escopo de implementação, mas é ator |
| **2 — Containers** | elementos executáveis e armazenadores: aplicação Spring Boot e banco; responsabilidades, tecnologias e comunicação | tecnologia e protocolo escritos em cada container e em cada seta |
| **3 — Componentes** | organização interna do container de aplicação: adaptadores de entrada, casos de uso, modelo, portas e adaptadores de saída | é aqui que a hexagonal da seção 8 precisa aparecer, com as setas sempre para dentro |

Nível 4 (código) **não** é exigido — o modelo de domínio em UML cobre isso.

Os diagramas precisam representar a arquitetura **efetivamente adotada** e
permanecer consistentes com a implementação. Diagrama que mostra uma porta que
o código não tem é erro de entrega, não detalhe.

Ferramenta sugerida: PlantUML (`.puml` versionado + `.png` exportado), porque o
fonte fica no Git e o diff mostra o que mudou. Structurizr também serve.

### 15.2 Registro de decisões (ADR)

O enunciado pede: **decisão adotada, principais alternativas consideradas e
justificativa**. Não exige formato. Um arquivo por decisão em
`docs/arquitetura/decisoes/`, no formato:

```markdown
# ADR 0001 — Arquitetura hexagonal (ports & adapters)

**Data:** AAAA-MM-DD
**Situação:** aceita

## Contexto
O que no problema motivou a decisão. Cita o requisito do enunciado.

## Alternativas consideradas
1. <alternativa> — vantagem, desvantagem, por que não foi escolhida.
2. ...

## Decisão
O que foi decidido, em uma ou duas frases.

## Consequências
O que isso facilita, o que isso custa, e o que passa a ser obrigatório
por causa dessa decisão.
```

Decisões que este trabalho **precisa** ter registradas:

1. Estilo arquitetural (hexagonal × camadas × outro).
2. Entidade de domínio separada da entidade de persistência — ou não.
3. Banco escolhido e por quê (e por que não em memória).
4. Como a atomicidade da aprovação é garantida (transação, constraint, lock).
5. Como o cálculo de slots foi modelado (value object `Slot`).
6. Como categoria nova de aeronave entra sem alterar a validação.
7. Estratégia de herança da hierarquia de aeronaves no banco.
8. Idioma do código (domínio em português).

Cada uma dessas nasce de uma entrada em `logs/decisoes.md`. O ADR é a versão
limpa; o log é o rascunho com data.

---

## 16. README, OpenAPI e apresentação

### 16.1 O `README.md`

O enunciado lista o que ele tem que explicar:

- [ ] como preparar o ambiente (Java, Maven, banco);
- [ ] como inicializar o banco;
- [ ] como carregar os dados iniciais;
- [ ] como compilar e executar;
- [ ] como acessar a API (URL base, link da documentação OpenAPI);
- [ ] **qual é a data de referência**;
- [ ] onde estão o modelo de domínio, os C4 e as decisões.

Critério: alguém que nunca viu o projeto clona e roda **sem perguntar nada**.
A solução tem que ser reproduzível sem depender do ambiente da equipe.

### 16.2 OpenAPI

Exigido: documentação das operações, parâmetros, requisições e respostas, por
OpenAPI ou equivalente. Com Spring Boot, `springdoc-openapi` resolve — e o
Swagger UI serve como a própria ferramenta de demonstração na apresentação.

Documentar também os **casos de erro**, não só o caminho feliz: a qualidade da
API "inclusive em situações de sucesso e erro" está na lista de critérios.

### 16.3 Roteiro da apresentação (19/10)

A demonstração usa a **data de referência** e os dados iniciais, e precisa
cobrir:

| # | O que demonstrar |
|---|---|
| 1 | Consultar a malha aérea |
| 2 | Criar plano e montar o percurso trecho a trecho |
| 3 | Consultar disponibilidade de altitudes e de vagas |
| 4 | Validar → mostrar que **não** criou reserva |
| 5 | Aprovar → mostrar as reservas criadas |
| 6 | **Falha: percurso inconsistente** (destino de uma aerovia ≠ origem da seguinte) |
| 7 | **Falha: horário incompatível** com a duração calculada |
| 8 | **Falha: restrição da aeronave** (particular a 30.000 pés; carga às 09:00) |
| 9 | **Falha: autonomia insuficiente** (sobrevoos acumulando) |
| 10 | **Falha: conflito de altitude** em aerovia já ocupada |
| 11 | **Falha: vaga indisponível** |
| 12 | **Aprovação malsucedida não deixa reserva parcial** |
| 13 | Cancelar plano aprovado **futuro** → reservas liberadas |
| 14 | Tentar cancelar plano com partida **já ocorrida** → recusado |
| 15 | Abandonar plano em edição |
| 16 | Consultar planos aprovados, cancelados e abandonados |
| 17 | Tentar modificar plano que não está em edição → recusado |

Guarda cada uma dessas requisições em `docs/api/requests.http`, numerada e
comentada. O professor pode pedir **requisições fora do roteiro** — por isso a
API precisa ser navegável e os dados precisam ter folga, não só os casos
ensaiados.

---

# Parte F: consulta rápida

## 17. Armadilhas do enunciado

As que custam nota e são fáceis de deixar passar:

| # | Armadilha | O que fazer |
|---|---|---|
| 1 | Tratar aerovia como editável | imutável após cadastro: sem `PUT`, sem `DELETE`, sem setter |
| 2 | Aerovia como ligação bidirecional | é **orientada**; POA→FLN e FLN→POA são registros distintos (R1 e R8) |
| 3 | Deixar o usuário informar o horário de chegada | é **calculado** pelo sistema; horário informado que não bate é erro |
| 4 | Esquecer que uso parcial ocupa o slot **inteiro** | 15:52–17:07 são 3 slots, não 1 |
| 5 | Sobrevoo zerando a autonomia | só **pouso** reabastece; sobrevoo acumula |
| 6 | Usar 100% da autonomia | o limite é **85%** |
| 7 | Esquecer o pouso obrigatório no destino final | e ele também informa vaga e permanência |
| 8 | Validar reservando | validar **não** reserva e **não** muda estado |
| 9 | Aprovação deixando reserva parcial | tudo ou nada, numa transação |
| 10 | Cancelar plano com partida passada | só plano **futuro** é cancelável |
| 11 | Permitir alterar plano aprovado/cancelado/abandonado | só EM_EDICAO é modificável |
| 12 | Voo terminando no dia seguinte | chegada ao destino final **na data da partida**; só a permanência final pode virar o dia |
| 13 | Malha aérea em constante no código | vem do banco; aeroporto e aerovia novos entram sem recompilar |
| 14 | `if` por categoria de aeronave espalhado | polimorfismo; categoria nova é o teste de extensão do professor |
| 15 | Menos de 5 aerovias ocupadas na data de referência | o mínimo é 60% de 8 |
| 16 | Reserva inserida direto no seed, sem plano | ocupação tem que ser **consequência de plano aprovado** |
| 17 | H2 em memória | o estado tem que sobreviver à reinicialização |
| 18 | Diagrama divergente do código | diagrama e implementação são avaliados **juntos** |

---

## 18. Checklists

**Preparação do repositório** — feito em 2026-09-30

- [x] Enunciado lido inteiro, do começo ao fim.
- [x] `git init` feito, `.gitignore` de Java/Maven no lugar.
- [x] Estrutura de `docs/` e `logs/` criada, com os logs já com a primeira entrada.
- [x] Material da PROJAQ localizado (seção 2), em especial o `P7a` hexagonal.
- [x] Padrões de branch, commit e PR definidos (`docs/padroes-desenvolvimento.md`).
- [x] Template de PR em `.github/pull_request_template.md`.
- [ ] Projeto Spring Boot criado em `backend/`, a partir das versões do `pom.xml` de exemplo.

**Antes de abrir o PR**

- [ ] Branch atualizada com `main`, sem conflito.
- [ ] `./mvnw clean test` e `./mvnw clean package` passando.
- [ ] Template preenchido com verdade, inclusive o que NÃO foi testado.
- [ ] Caso(s) de uso e entrega do cronograma citados no corpo.
- [ ] Diagrama e ADR atualizados, se a mudança pediu.
- [ ] Entradas nos dois logs feitas.

**Entrega de 05/10 — domínio**

- [ ] Modelo de domínio em UML, com atributos, operações e cardinalidades.
- [ ] Value objects e raiz de agregado identificados.
- [ ] Regras do enunciado mapeadas para onde moram no modelo.
- [ ] Casos de uso numerados, com ator, pré-condição e resultado.
- [ ] `.puml` e `.png` versionados em `docs/dominio/`.
- [ ] Decisões de modelagem registradas em `decisoes.md`.

**Entrega de 07/10 — arquitetura**

- [ ] C4 nível 1, 2 e 3, com tecnologias nas setas.
- [ ] Estrutura de pastas criada no repositório, não só desenhada.
- [ ] Regra de dependência escrita e respeitada.
- [ ] ADR 0001 (estilo arquitetural) escrito, com alternativas.

**Entrega de 14/10 — 40% dos casos de uso**

- [ ] Ciclo completo de um plano funcionando pela API.
- [ ] Banco persistindo entre reinicializações.
- [ ] Carga inicial rodando do zero.
- [ ] Testes das regras de cálculo passando.
- [ ] Quais UCs estão prontos, listados no `log.md`.

**Antes de cada commit**

- [ ] Autorrevisão da seção 11 feita.
- [ ] `./mvnw clean test` limpo depois da última alteração.
- [ ] `git status` mostra só os arquivos da mudança.
- [ ] Mensagem em Conventional Commits.
- [ ] Diagrama atualizado, se a mudança mexeu no domínio.

**Fim de sessão**

- [ ] Nada sem commit que eu não saiba.
- [ ] Entrada no `log.md`.
- [ ] Entrada no `decisoes.md`, se houve decisão.
- [ ] "Retomar por aqui" escrito.

**Antes da apresentação de 19/10**

- [ ] Banco apagado e recriado do zero, com sucesso.
- [ ] As 17 demonstrações da seção 16.3 testadas, em ordem.
- [ ] Data de referência conferida: 5+ aerovias com ocupação.
- [ ] `requests.http` (ou coleção equivalente) completo e numerado.
- [ ] Swagger UI abrindo.
- [ ] README seguido por alguém de fora, do clone até a primeira requisição.
- [ ] Diagramas conferidos contra o código, um por um.
- [ ] ADRs escritos, cobrindo as 8 decisões da seção 15.2.
