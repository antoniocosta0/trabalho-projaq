# Contexto do projeto — Sistema de Gestão de Planos de Voo

Trabalho Final (parte 1) da cadeira **Projeto e Arquitetura de Software**
(PROJAQ) — PUCRS, 4º semestre, 2026/2, Prof. Bernardo Copstein.

Backend em **Java + Spring Boot**, com API REST, persistência em banco de
dados, modelo de domínio em UML e documentação de arquitetura em C4.

---

## Como usar este arquivo

Este é um arquivo **autocontido**. Ele não depende de nenhuma pasta, caminho
ou material baixado na máquina de ninguém: tudo o que é preciso saber sobre a
cadeira, sobre o enunciado e sobre as convenções do projeto está escrito aqui
dentro.

**Se você é uma IA assistente de código** (Claude Code, Cursor, Copilot,
Gemini CLI, Codex…): leia este arquivo inteiro antes de escrever qualquer
linha. Ele contém o vocabulário do domínio, a arquitetura adotada, as regras de
negócio completas e as convenções de código. Para que ele seja carregado
automaticamente, copie-o com o nome que a sua ferramenta usa:

| Ferramenta | Nome do arquivo |
|---|---|
| Claude Code | `CLAUDE.md` |
| Cursor | `.cursorrules` ou `.cursor/rules/projeto.mdc` |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Gemini CLI | `GEMINI.md` |
| Codex / outras | `AGENTS.md` |

**Se você é uma pessoa:** as Partes 1 e 2 são para ler uma vez, inteiras. As
Partes 3 a 6 são de consulta. A Parte 7 tem os checklists.

**Duas regras acima de tudo:**

1. **O enunciado vence.** Onde este arquivo divergir do enunciado oficial, o
   enunciado está certo e este arquivo é corrigido.
2. **O que não está registrado não existe.** Toda sessão de trabalho termina
   com entrada nos logs (Parte 5).

---

## Sumário

**Parte 1 — A cadeira**

1. [O que é PROJAQ e por que isso importa aqui](#1-o-que-é-projaq-e-por-que-isso-importa-aqui)
2. [Conteúdo 1: produtos de software e arquitetura](#2-conteúdo-1-produtos-de-software-e-arquitetura)
3. [Conteúdo 2: princípios de projeto](#3-conteúdo-2-princípios-de-projeto)
4. [Conteúdo 3: padrões de projeto (GoF)](#4-conteúdo-3-padrões-de-projeto-gof)
5. [Conteúdo 4: Spring Boot](#5-conteúdo-4-spring-boot)
6. [Conteúdo 5: evolução dos estilos arquiteturais](#6-conteúdo-5-evolução-dos-estilos-arquiteturais)

**Parte 2 — O trabalho**

7. [Visão geral e escopo](#7-visão-geral-e-escopo)
8. [Regras de negócio completas](#8-regras-de-negócio-completas)
9. [Modelo de domínio](#9-modelo-de-domínio)
10. [Casos de uso](#10-casos-de-uso)

**Parte 3 — Arquitetura e código**

11. [Arquitetura adotada e estrutura de pastas](#11-arquitetura-adotada-e-estrutura-de-pastas)
12. [Padrões de desenvolvimento](#12-padrões-de-desenvolvimento)
13. [Testes](#13-testes)

**Parte 4 — Dados**

14. [Banco de dados e carga inicial](#14-banco-de-dados-e-carga-inicial)

**Parte 5 — Processo**

15. [Registro do trabalho: os dois logs](#15-registro-do-trabalho-os-dois-logs)

**Parte 6 — Entrega**

16. [Cronograma e artefatos exigidos](#16-cronograma-e-artefatos-exigidos)
17. [Apresentação](#17-apresentação)

**Parte 7 — Consulta rápida**

18. [Armadilhas do enunciado](#18-armadilhas-do-enunciado)
19. [Checklists](#19-checklists)
20. [Glossário](#20-glossário)

---

# Parte 1 — A cadeira

## 1. O que é PROJAQ e por que isso importa aqui

PROJAQ trata de **como organizar um sistema de software** para que ele seja
fácil de entender, de manter e de evoluir. A pergunta central da cadeira não é
"isso funciona?", e sim "isso continua funcionando quando o requisito mudar, e
alguém consegue mexer nisso daqui a seis meses?".

A cadeira caminha em cinco blocos, nesta ordem:

| # | Bloco | Pergunta que responde |
|---|---|---|
| 1 | Produtos de software e arquitetura | o que é arquitetura e por que ela existe |
| 2 | Princípios de projeto | o que torna um projeto bom ou ruim |
| 3 | Padrões de projeto (GoF) | soluções já conhecidas para problemas recorrentes |
| 4 | Spring Boot | a tecnologia usada para construir o backend |
| 5 | Evolução dos estilos arquiteturais | monolito → camadas → DDD → hexagonal |

**Por que isso importa neste trabalho, concretamente:** a avaliação não é só
"a API responde". Os critérios do enunciado incluem adequação do estilo
arquitetural escolhido, separação de responsabilidades, qualidade do modelo de
domínio, coerência entre documentação e implementação, e capacidade de
acomodar as evoluções previstas. Ou seja: **o professor avalia o projeto contra
o que ele ensinou**, e usando o vocabulário dele.

Por isso, ao longo deste arquivo, aparece com frequência a instrução de usar
determinados termos (portas, adaptadores, casos de uso, agregado, value object)
no código e nos diagramas. Não é preciosismo: é a forma de a solução ser
legível para quem a corrige.

Durante o semestre houve também duas provas (P1 e P2) e um "concurso de
padrões" em aula. Nada disso afeta este trabalho, mas explica por que o
material está organizado em blocos.

---

## 2. Conteúdo 1: produtos de software e arquitetura

O bloco de abertura, baseado no capítulo 1 do Sommerville ("Produtos de
Software") e em discussões sobre o papel do arquiteto.

**Programa × sistema.** Um programa resolve o problema de quem o escreveu. Um
sistema de software é usado por outras pessoas, em situações que o autor não
previu, e precisa continuar de pé quando quem o escreveu já saiu. A diferença
não é tamanho: é a existência de outros interessados.

**O que é arquitetura.** A definição usada na cadeira: os **componentes** de um
sistema, os **relacionamentos** entre eles e os **princípios** que governam seu
projeto e sua evolução. O objetivo prático é um só: **um sistema fácil de
manter**.

**Software customizado × produto.** Software customizado é feito para um
cliente específico, que define os requisitos. Produto é feito para um mercado,
e quem define o escopo é um gestor de produto, apoiado em prototipação e
feedback. Este trabalho é customizado: o "cliente" é o enunciado, e ele é
explícito sobre o que quer.

**Os casos discutidos.** Dois exemplos reais serviram para mostrar que decisão
de arquitetura tem consequência fora do código — uma rede social que perdeu a
hospedagem por depender inteiramente de um provedor, e um caso em que a
impossibilidade de alterar um dado pessoal (um nome) revelou uma decisão de
modelagem tratada como imutável sem necessidade.

A lição que interessa aqui: **o que você decide tornar imutável, e o que você
decide acoplar, vira uma restrição permanente**. Neste trabalho isso aparece
literalmente: o enunciado manda que **aerovia seja imutável após o cadastro** —
e essa é uma decisão de modelagem, não um detalhe de API.

---

## 3. Conteúdo 2: princípios de projeto

Os princípios são os critérios para julgar se um projeto está bom, antes de
qualquer padrão ou framework entrar em cena. São cinco, e todos aparecem na
avaliação deste trabalho.

### 3.1 Decomposição

Dividir o sistema em partes, de modo que cada uma possa ser entendida e
modificada sozinha. O exemplo clássico é o de um compilador, que se divide em
etapas (análise léxica, sintática, semântica, geração de código) com interfaces
claras entre elas.

O ponto fino: decompor mal é pior do que não decompor. Uma divisão que obriga
a mexer em cinco módulos para fazer uma mudança simples não decompôs nada — só
espalhou.

### 3.2 Integridade conceitual

O sistema inteiro deve parecer ter sido projetado por uma só cabeça. Duas
coisas que fazem a mesma função devem ser feitas do mesmo jeito, ter nomes
parecidos e se comportar de forma previsível — tanto para quem usa quanto para
quem desenvolve.

**Neste trabalho:** se `GET /planos/{id}` devolve 404 para plano inexistente,
`GET /aeronaves/{prefixo}` também devolve. Se uma validação devolve lista de
problemas, todas devolvem. Se o domínio está em português, está em português em
todo lugar.

### 3.3 Ocultamento de informação

Um módulo expõe **a interface pública e nada mais**. Tudo o que é decisão
interna fica escondido, para que possa mudar sem quebrar quem depende dele.

**Neste trabalho:** o fato de a ocupação ser controlada em slots de uma hora é
decisão interna do domínio. Quem consulta a API pergunta "esta altitude está
livre entre tal e tal hora?" — não precisa saber o que é um slot. Se amanhã o
controle virar de meia hora, a API não muda.

### 3.4 Coesão

Cada módulo tem **uma** responsabilidade. Uma classe que calcula rota, grava no
banco e formata JSON tem três, e vai mudar por três motivos diferentes.

**Neste trabalho:** `Slot` só sabe sobre faixas de hora cheia. `Aerovia` só
sabe sobre a ligação entre dois aeroportos e sua distância. `PlanoDeVoo`
coordena o percurso. O controller só traduz HTTP.

### 3.5 Acoplamento

Quanto um módulo depende de outro. A cadeira distingue:

| | Descrição | Exemplo |
|---|---|---|
| **Aceitável** | depender de uma **abstração** estável | o domínio depende da interface `PlanoRepository` |
| **Ruim** | depender de um **detalhe** que pode mudar | o domínio depende de `JpaPlanoRepository`, ou importa `jakarta.persistence` |
| **Estrutural** | A chama B diretamente | visível no código |
| **Evolutivo** | A e B precisam mudar juntos, mesmo sem se chamarem | invisível, e o mais perigoso |

O acoplamento evolutivo é o que pega. Exemplo deste trabalho: se a regra de
"85% da autonomia" estiver escrita em dois lugares (no domínio e numa
validação do controller), não há chamada entre eles — mas mudar a margem exige
mexer nos dois. Isso é acoplamento, e é um defeito.

**SOLID** não foi coberto em aula. Os princípios acima dão conta do que a
avaliação cobra.

---

## 4. Conteúdo 3: padrões de projeto (GoF)

Padrões são soluções conhecidas para problemas recorrentes de projeto,
catalogadas pelo livro *Design Patterns* (Gamma, Helm, Johnson e Vlissides,
1995 — "a Gangue dos Quatro"). A frase que resume o bloco: **reúso de ideias,
não de código**. O ganho principal é um **vocabulário comum** — dizer "aqui é
um Strategy" economiza um parágrafo de explicação.

**Aviso que vale mais que a lista:** padrão colocado para aparecer no relatório
é acoplamento de graça. Use quando resolver um problema real, e esteja pronto
para explicar qual.

### 4.1 Padrões de criação — como instanciar e inicializar

| Padrão | O que faz | Onde cabe neste trabalho |
|---|---|---|
| **Static Factory Method** | método estático nomeado que cria o objeto, em vez do construtor | `Slot.entre(inicio, fim)`, `Slot.de(hora)` — o nome diz o que está sendo criado |
| **Factory** | esconde de quem chama qual subclasse está sendo criada | criar a aeronave certa (particular, passageiros, carga) a partir do DTO, sem o controller conhecer as subclasses |
| **Builder** | constrói um objeto complexo por partes, com interface fluente | montagem progressiva do plano de voo, que é exatamente o fluxo do enunciado |
| **Singleton** | garante uma única instância | **evitar.** A cadeira é explícita: esconde dependências e dificulta teste. Com Spring, o container já dá instância única de bean — não há motivo para implementar à mão |

### 4.2 Padrões comportamentais — como os objetos se comunicam

| Padrão | O que faz | Onde cabe neste trabalho |
|---|---|---|
| **Observer** | uma fonte notifica vários interessados quando algo muda | não há evento assíncrono aqui; provavelmente não cabe |
| **Strategy** | permite trocar o algoritmo em tempo de execução | **o mais útil aqui**: a regra de operação varia por categoria de aeronave (altitudes permitidas, janela de horário) |
| **Chain of Responsibility** | passa a requisição por uma cadeia até alguém tratar | cabe para a validação: cada regra é um elo, e o plano passa por todos acumulando problemas |

### 4.3 Padrões estruturais — como compor objetos

| Padrão | O que faz | Onde cabe neste trabalho |
|---|---|---|
| **Adapter** | traduz uma interface na outra | **é a base da arquitetura hexagonal**: os adaptadores REST e de persistência existem exatamente para isso |
| **Decorator** | acrescenta comportamento sem alterar a classe | não é necessário aqui |
| **Proxy** | controla o acesso, com verificação ou cache | não é necessário aqui |
| **Facade** | dá uma interface simples para um subsistema complicado | os casos de uso funcionam como fachada do domínio para os controllers |
| **Composite** | trata objeto individual e composição de objetos do mesmo jeito | não foi visto em aula; só caberia se o percurso virasse estrutura aninhada, o que não é o caso |

**Resumo prático para este trabalho:** Strategy (ou polimorfismo direto) para
regra de aeronave; Factory para criação por categoria; Builder para montar o
plano; Adapter como consequência da arquitetura. Os demais, só se aparecer um
problema que os peça.

---

## 5. Conteúdo 4: Spring Boot

A tecnologia obrigatória do trabalho. O que a cadeira cobriu:

### 5.1 Web e HTTP

Modelo cliente-servidor; métodos HTTP (`GET`, `POST`, `PUT`, `DELETE`); as três
formas de o cliente mandar dado — **path parameter** (`/planos/42`), **query
string** (`/aerovias?origem=POA`) e **corpo da requisição** (JSON no `POST`).
Postman e similares como cliente de teste.

### 5.2 Controller REST

Uma classe anotada com `@RestController` expõe rotas:

```java
@RestController
@RequestMapping("/planos")
public class PlanoController {

    @GetMapping("/{id}")
    public PlanoResponse buscar(@PathVariable Long id) { ... }

    @PostMapping
    public ResponseEntity<PlanoResponse> criar(@RequestBody CriarPlanoRequest request) { ... }
}
```

O que a cadeira enfatiza: o controller **traduz HTTP**. Ele recebe, chama quem
sabe fazer, e devolve — sem regra de negócio e sem consulta ao banco.

### 5.3 Injeção de dependência e inversão de controle

**Injeção de dependência (DI):** a classe não cria suas dependências; recebe
prontas. **Inversão de controle (IoC):** quem decide quem recebe o quê é o
*container* do Spring, não o código.

Na prática: você anota as classes como beans (`@Component`, `@Service`,
`@Repository`, `@RestController`) e o Spring as instancia e liga sozinho.

**Injeção por construtor**, sempre — é o que permite instanciar a classe num
teste sem subir o Spring:

```java
@Service
public class AprovarPlanoService implements AprovarPlanoUseCase {

    private final PlanoRepository planoRepository;

    public AprovarPlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }
}
```

Quando há mais de uma implementação da mesma interface, o Spring não sabe qual
usar: resolve-se por tipo, por `@Primary` (a padrão) ou por `@Qualifier` (a
escolhida explicitamente).

### 5.4 Persistência

Duas abordagens foram cobertas:

**JDBC** — SQL escrito à mão, com a classe de repositório anotada
`@Repository`, scripts de schema em `src/main/resources` e configuração em
`application.properties`. Mais controle, mais código.

**JPA / ORM (Data Mapper)** — as tabelas são descritas por classes anotadas
(`@Entity`, `@Id`, `@GeneratedValue`), e as interfaces que estendem
`JpaRepository` ganham `save`, `findById`, `findAll` e consultas derivadas do
nome do método, sem implementação. Relacionamentos com `@OneToMany`,
`@ManyToOne`, `@ManyToMany`. H2 foi o banco usado em aula.

**Decisão deste trabalho:** JPA, pelo volume de entidades e relacionamentos.
Fica registrado como decisão arquitetural, com JDBC como alternativa
considerada.

### 5.5 Autenticação básica

Coberto ao final: `401` quando falta credencial, cabeçalho `Authorization` com
usuário e senha em Base64 — que **não é criptografia**, e por isso só se usa
sobre HTTPS. **Não é exigido neste trabalho**; só entra se a equipe quiser, e
aí vira decisão registrada.

### 5.6 Versões

O projeto parte das mesmas versões usadas nos exemplos da cadeira:
**Spring Boot 4.1.1** e **Java 25**. Confira o `pom.xml` em `backend/` antes de
assumir outra coisa.

---

## 6. Conteúdo 5: evolução dos estilos arquiteturais

O bloco mais importante para este trabalho. A linha do raciocínio é uma
evolução: cada estilo aparece para resolver um problema que o anterior deixou.

### 6.1 O contexto: aplicações corporativas

O foco da cadeira são **aplicações corporativas** — sistemas que automatizam o
dia a dia de uma organização. Elas costumam ter: perfis diferentes de usuário,
várias formas de interface, usuários e sistemas distribuídos, grandes volumes
de dados e de transações, e algoritmos de missão crítica.

O sistema de planos de voo é exatamente isso em miniatura: várias entidades
relacionadas, regras de negócio densas, transações que precisam ser atômicas, e
consultas de disponibilidade sobre volume.

### 6.2 Estilo em camadas

A organização mais difundida. O sistema é dividido em camadas horizontais, cada
uma usando apenas a de baixo:

```
Apresentação  (interface, rotas HTTP)
      ↓
Aplicação / Negócio  (regras)
      ↓
Persistência  (acesso a dados)
      ↓
Banco de dados
```

**O que ela resolve:** separa responsabilidades, permite trocar a interface sem
mexer na regra, e é fácil de entender.

**O problema que ela deixa:** a seta aponta para baixo, ou seja, **a camada de
negócio depende da camada de persistência**. Na prática, isso faz a regra de
negócio nascer moldada pelo banco — a classe de domínio vira um espelho da
tabela, com `getters` e `setters` e nenhum comportamento, e a regra escorre
para serviços gigantes. O domínio acaba sendo consequência do modelo
relacional, em vez de o contrário.

### 6.3 Domain-Driven Design (DDD)

O DDD inverte essa lógica. A proposta: **parar de construir a aplicação a
partir do projeto do banco de dados** e começar pelo domínio.

**Primeiro passo: conhecer o domínio.** Antes de modelar, entender a área de
aplicação. A formulação usada em aula é direta: não dá para construir um
sistema de controle de tráfego aéreo sem conhecer o jargão da área.

**Linguagem ubíqua.** Estabelecer uma linguagem de domínio — os mesmos termos
usados pelos especialistas, pelos documentos e pelo código. Se o enunciado diz
"sobrevoo", o código diz `SOBREVOO`, e não `FLY_OVER` nem `tipo = 2`.

> É por isso que neste trabalho o **domínio é escrito em português**. Traduzir
> `PlanoDeVoo` para `FlightPlan` quebraria a correspondência direta entre o
> enunciado, o diagrama e o código — que é justamente o que a avaliação cobra.

**Os blocos de montar.** O DDD dá nome aos tipos de elemento do modelo:

| Bloco | O que é | Exemplo aqui |
|---|---|---|
| **Entidade** | tem identidade própria, que persiste mesmo quando os atributos mudam | `PlanoDeVoo` (tem id), `Aerovia`, `Aeroporto`, `Aeronave`, `Piloto` |
| **Value object** | não tem identidade; é definido pelos seus valores e é imutável | `Slot` (uma faixa de hora), `Pouso` (vaga + permanência) |
| **Agregado** | um conjunto de objetos tratado como uma unidade de consistência, com uma **raiz** que é a única porta de entrada | `PlanoDeVoo` é a raiz; `Trecho` só existe dentro dele |
| **Repositório** | abstrai a recuperação e a guarda de agregados, como se fosse uma coleção em memória | `PlanoRepository` (a **interface**, no domínio) |
| **Serviço de domínio** | uma operação do domínio que não pertence naturalmente a nenhuma entidade | verificar disponibilidade de altitude numa aerovia |
| **Factory** | encapsula a criação de um agregado complexo | criação de aeronave por categoria |

**Por que agregado importa aqui:** a aprovação de um plano tem que ser atômica
— ou reserva tudo, ou nada. Ter `PlanoDeVoo` como raiz de agregado é o que dá
uma fronteira natural para essa transação. Se `Trecho` pudesse ser alterado por
fora, não haveria onde garantir a consistência.

### 6.4 Arquitetura hexagonal (ports & adapters)

O DDD diz **o que** modelar. A hexagonal diz **onde colocar**, de forma que o
modelo não seja contaminado pela tecnologia.

**O objetivo, na formulação da cadeira:** *isolar o modelo do domínio das
tecnologias necessárias para a implementação do sistema*.

A ideia: o domínio fica no centro e **não conhece nada** do mundo externo. Toda
comunicação com o exterior passa por **portas** (interfaces definidas pelo
domínio), e cada tecnologia entra por um **adaptador** que implementa ou aciona
uma porta.

```
                   ┌──────────────────────────────┐
   HTTP  ──────►   │  adaptador de entrada (REST) │
                   └──────────────┬───────────────┘
                                  │ aciona
                   ┌──────────────▼───────────────┐
                   │  porta de entrada: CASO DE USO│
                   └──────────────┬───────────────┘
                                  │ orquestra
                   ┌──────────────▼───────────────┐
                   │          MODELO               │
                   │  entidades, VOs, serviços     │
                   └──────────────┬───────────────┘
                                  │ usa
                   ┌──────────────▼───────────────┐
                   │  porta de saída: REPOSITÓRIO  │
                   └──────────────┬───────────────┘
                                  │ implementada por
                   ┌──────────────▼───────────────┐
  Banco  ◄──────   │ adaptador de saída (JPA)      │
                   └──────────────────────────────┘
```

**O fluxo de uma requisição**, nos cinco passos do material da cadeira:

| # | Elemento | Responsabilidade |
|---|---|---|
| 01 | Adaptador de entrada (controller) | fala HTTP e a linguagem do mundo externo (DTOs); aciona a porta de entrada |
| 02 | Porta de entrada (caso de uso) | orquestra a lógica da aplicação usando os elementos do modelo |
| 03 | Modelo | define as abstrações que representam as entidades e capacidades de negócio do domínio |
| 04 | Portas de saída | abstraem o acesso externo; são usadas pelos elementos do modelo |
| 05 | Adaptadores de saída | implementam as abstrações definidas pelas portas de saída |

**A inversão que faz tudo funcionar:** a interface `PlanoRepository` é definida
**no domínio**, e implementada **na infraestrutura**. O domínio declara o que
precisa; a tecnologia se adapta a ele. Compare com o estilo em camadas, onde o
negócio dependia da persistência — aqui a seta se inverte, e é por isso que o
domínio fica testável sem banco e sem framework.

**O preço.** A hexagonal custa código: uma interface a mais por repositório,
uma classe de entidade de persistência separada da entidade de domínio, e um
mapeador entre as duas. Isso é real, e precisa ser reconhecido na justificativa
— não escondido. O ganho que compra esse custo é o isolamento, e ele só existe
se a regra de dependência for respeitada de verdade.

### 6.5 Qual escolher — e como justificar

O enunciado **não exige** um estilo específico. Exige que a equipe explique e
justifique a escolha em relação aos requisitos. As duas justificativas honestas:

- **Hexagonal:** o domínio é denso em regras (validação, autonomia, slots,
  categorias de aeronave), o enunciado prevê explicitamente evolução (novas
  categorias, novas regras, novos aeroportos), e cobra "facilidade de verificar
  o comportamento". Isolar o domínio entrega as três coisas. Custa mais código.
- **Camadas:** é mais simples, mais rápido de escrever e todo mundo entende. Em
  troca, o domínio tende a ficar anêmico e os testes exigem infraestrutura.

**Este projeto adotou a hexagonal.** A justificativa, com as alternativas,
precisa estar escrita no registro de decisões (Parte 6).

---

# Parte 2 — O trabalho

## 7. Visão geral e escopo

Projetar e implementar o **backend** de um sistema que cria, valida e gerencia
planos de voo.

**O que o sistema faz:** o usuário monta um plano de voo trecho a trecho —
informando a aerovia, a altitude, o horário de entrada e o que acontece ao
chegar ao aeroporto de destino daquele trecho. O sistema **verifica** se o
percurso é contínuo, se os horários batem com a duração calculada, se a
aeronave pode operar naquela altitude e naquele horário, se a autonomia
aguenta, e se altitude e vaga estão livres — e só então **reserva tudo de uma
vez** e aprova.

**O que o sistema não faz:**

- não calcula nem sugere rota — o usuário informa a sequência de aerovias;
- não trata habilitação, licença ou jornada de piloto;
- não tem interface gráfica: tudo é exposto por **API REST**.

**O que é avaliado** (lista do enunciado): atendimento às regras de negócio;
qualidade do modelo de domínio; adequação do estilo arquitetural; separação de
responsabilidades; coerência entre domínio, arquitetura, documentação e
implementação; qualidade dos diagramas C4; qualidade da API, inclusive nos
casos de erro; uso adequado do banco; consistência das operações com múltiplas
alterações; capacidade de acomodar evoluções; clareza do código; e qualidade da
documentação e das justificativas.

---

## 8. Regras de negócio completas

Esta seção é a referência operacional. Onde ela divergir do enunciado oficial,
o enunciado vence.

### 8.1 Malha aérea inicial

Aeroportos — cada um tem código único, nome e quantidade de vagas de
estacionamento. Qualquer vaga serve para qualquer aeronave.

| Código | Aeroporto | Vagas |
|---|---|---|
| POA | Salgado Filho — Porto Alegre | 41 |
| FLN | Hercílio Luz — Florianópolis | 16 |
| CWB | Afonso Pena — Curitiba | 26 |
| GRU | Guarulhos — São Paulo | 115 |

Aerovias — ligação **orientada** entre dois aeroportos, com id único, origem,
destino e distância em km. A→B **não** implica B→A: são registros distintos.

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

**Aerovia é imutável após o cadastro.** Não se altera e não se exclui; só se
cria nova, com id novo. Isso preserva os dados referenciados por planos e
reservas. É restrição de modelagem, não só de API: nada de `PUT /aerovias/{id}`
e nada de setter público em `Aerovia`. Aerovia em desuso permanece cadastrada.

**Novos aeroportos e aerovias podem ser informados pelo professor** e têm que
entrar **sem alteração na lógica da aplicação**. Ou seja: a malha vem do banco,
nunca de constante no código.

O sistema deve permitir consultar e cadastrar aeroportos e aerovias.

### 8.2 Aeronaves e regras de operação

Toda aeronave tem prefixo único, velocidade de cruzeiro (km/h) e autonomia (km).

| Categoria | Dados próprios | Altitudes permitidas | Janela de horário |
|---|---|---|---|
| Particular | empresa responsável pela manutenção | **somente** 25.000, 26.000 e 27.000 pés | qualquer |
| Comercial de passageiros | companhia aérea, capacidade de passageiros | **≥ 28.000 pés**, dentro da faixa permitida | qualquer |
| Comercial de carga | companhia aérea, capacidade em toneladas | qualquer altitude prevista | **todo** o período de uso da aerovia entre **00:00 (inclusive) e 06:00 (exclusive)** |

**Novas categorias e novas regras podem ser informadas durante o
desenvolvimento, e a solução deve acomodá-las sem modificações extensas.** Esse
é o ponto de extensão que será testado. A resposta de projeto: a pergunta "esta
aeronave pode operar nesta altitude, neste intervalo?" é **uma operação
polimórfica na hierarquia de aeronave** (ou uma Strategy por categoria), nunca
um `if`/`switch` por categoria espalhado pelos serviços.

### 8.3 Autonomia

- A autonomia é a distância máxima desde o último abastecimento.
- Margem de segurança de 15%: a distância percorrida sem reabastecer não pode
  passar de **85% da autonomia nominal**.
- **Sobrevoo acumula** distância.
- **Pouso reabastece**: considera-se que a aeronave é reabastecida antes de
  prosseguir, zerando o acumulado.

### 8.4 Horários

```
duração do trecho  = distância da aerovia / velocidade de cruzeiro da aeronave
horário de chegada = horário de entrada + duração
```

A velocidade é constante. O **horário de chegada é calculado pelo sistema**,
nunca informado pelo usuário.

| Situação | Horário de entrada na aerovia seguinte |
|---|---|
| Primeiro trecho | = horário de partida do plano |
| Depois de **sobrevoo** | = chegada calculada |
| Depois de **pouso intermediário** | = chegada calculada **+ tempo de permanência** |

### 8.5 Percurso

O percurso é uma **sequência ordenada de trechos**. Cada trecho informa:
aerovia, altitude, horário de entrada e o que ocorrerá ao chegar ao destino
(**SOBREVOO** ou **POUSO**).

Continuidade exigida:

- origem da primeira aerovia = origem do plano;
- destino de cada aerovia = origem da seguinte;
- destino da última aerovia = destino do plano.

Exemplo: `POA ──R1──> FLN ──R4──> CWB`

Regras adicionais:

- **No destino final o pouso é obrigatório.**
- Todo pouso informa a **vaga pretendida** e o **tempo de permanência em
  minutos** — inclusive o do destino final.
- Indicar uma vaga durante a elaboração **não constitui reserva**.
- **O voo inicia e termina no mesmo dia**: a chegada ao destino final ocorre na
  data da partida. Apenas a **permanência no destino final** pode se estender
  para o dia seguinte, e a reserva da vaga cobre todos os slots necessários.
- O sistema **não** procura nem propõe rota alternativa.

### 8.6 Slots e controle de ocupação

Altitudes disponíveis: de **25.000 a 35.000 pés**, inclusive, em incrementos de
1.000 pés — **11 altitudes**.

A ocupação é controlada em **slots de uma hora alinhados à hora cheia**, e
**qualquer utilização parcial ocupa o slot inteiro**.

| Exemplo | Slots ocupados |
|---|---|
| Aerovia: entrada 15:52, saída 17:07 | `15:00–16:00`, `16:00–17:00`, `17:00–18:00` |
| Vaga: chegada 15:50, permanência de 20 min | `15:00–16:00`, `16:00–17:00` |

Conflito = coincidência em **pelo menos um** slot, na mesma altitude da mesma
aerovia, ou na mesma vaga do mesmo aeroporto. A vaga precisa estar disponível
em **todos** os slots necessários.

> O cálculo de slots é a regra que mais se erra, por um slot a mais ou a menos
> na borda. Isole num value object `Slot` com uma operação do tipo
> `Slot.entre(inicio, fim)`, e cubra com teste unitário **antes** de ligar no
> resto do sistema.

**Consulta de disponibilidade.** Durante a elaboração, deve ser possível
consultar, para um período: as altitudes disponíveis numa aerovia e as vagas
disponíveis num aeroporto. A consulta reflete a situação **naquele momento** e
**não reserva** nada.

**Reserva e liberação.** Planos em edição não reservam recursos. As reservas são
efetivadas **somente na aprovação**, que é **integral**: ou todos os recursos
são reservados e o plano é aprovado, ou nenhuma reserva daquela tentativa
permanece e o plano continua em edição. Cancelar um plano aprovado futuro libera
todas as suas reservas; abandonar um plano em edição não exige liberação.

### 8.7 Estados do plano

Um plano tem identificador único e informa piloto, aeronave, aeroporto de
origem, aeroporto de destino e data/hora previstas de partida. Pilotos têm
apenas id e nome.

```
                  aprovar (todas as regras satisfeitas
                   e todos os recursos reservados)
   EM_EDICAO ──────────────────────────────────────────►  APROVADO
       │                                                      │
       │ abandonar                   cancelar (somente se a partida ainda
       ▼                              não ocorreu; libera as reservas)
   ABANDONADO                                                 ▼
                                                          CANCELADO
```

| Regra | Detalhe |
|---|---|
| Nasce | `EM_EDICAO` |
| Só `EM_EDICAO` pode ser modificado | os demais preservam as informações que tinham ao atingir o estado |
| `EM_EDICAO` não reserva nada | nem mesmo ao validar |
| Aprovação é **integral** | tudo ou nada |
| Abandono | só de `EM_EDICAO`; não há reserva a liberar |
| Cancelamento | só de `APROVADO` **com partida ainda futura**; libera todas as reservas |
| Partida já ocorrida | é um voo **já executado**: consultável, mas **não cancelável** |
| Consulta | por id e por situação; cancelados e abandonados continuam consultáveis |

### 8.8 Validação × aprovação

São operações **distintas**.

**Validar** não altera o estado e não reserva nada. Um plano pode ser validado,
alterado e validado de novo. A validação verifica, no mínimo:

- existência de um ou mais trechos;
- coerência entre a origem do plano e a primeira aerovia;
- coerência entre o destino do plano e a última aerovia;
- continuidade entre aerovias consecutivas;
- correspondência entre a entrada na primeira aerovia e a partida do plano;
- duração e chegada de cada trecho calculadas a partir da distância e da
  velocidade;
- após sobrevoo, correspondência entre a chegada e a entrada na próxima;
- após pouso intermediário, correspondência entre chegada + permanência e a
  entrada na próxima;
- pouso obrigatório no destino final;
- início e término do voo na mesma data;
- possibilidade de operação da aeronave na altitude e durante todo o intervalo;
- limite de 85% da autonomia desde o último abastecimento;
- disponibilidade da altitude em todos os slots de cada trecho;
- disponibilidade da vaga em todos os slots de cada permanência.

**Aprovar** reverifica as condições (ou garante que continuam válidas no
momento das reservas) e efetiva tudo numa transação. A disponibilidade
consultada antes pode ter mudado — por isso a aprovação considera a situação
**no momento da operação**.

> Implicação de projeto: `validar()` devolve uma **lista de problemas**, não um
> `boolean` e não uma exceção no primeiro erro. A API precisa dizer *o que*
> está errado, e o enunciado cobra a demonstração de vários tipos de falha.

---

## 9. Modelo de domínio

Estado atual do modelo. Muda com justificativa registrada.

| Elemento | Tipo | Papel |
|---|---|---|
| `PlanoDeVoo` | entidade / **raiz de agregado** | id, partida, situação; `adicionarTrecho`, `removerTrecho`, `validar() : List<String>`, `aprovar`, `abandonar`, `cancelar(agora)` |
| `Trecho` | entidade | ordem, altitude, entrada, ação; `chegada()`, `slotsOcupados()` |
| `Pouso` | **value object** (0..1 por trecho) | numeroVaga, permanenciaMin; `saida(chegada)` |
| `Aerovia` | entidade | id, distanciaKm, origem, destino; `duracao(velocidadeKmh) : Duration`; **imutável após o cadastro** |
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
| `SituacaoPlano` | enum | `EM_EDICAO`, `APROVADO`, `CANCELADO`, `ABANDONADO` |
| `AcaoNoDestino` | enum | `SOBREVOO`, `POUSO` |

**Decisões que o modelo embute** — todas precisam estar escritas, porque serão
questionadas:

1. `podeOperar` é **polimórfico na aeronave**, não um `if` por categoria no
   serviço. É assim que categoria nova entra sem mexer na validação.
2. `Slot` e `Pouso` são **value objects**: sem identidade, comparados por valor,
   imutáveis.
3. `PlanoDeVoo` é **raiz de agregado**: `Trecho` só existe dentro dele, e toda
   alteração passa pelo plano. É o que dá fronteira para a transação de
   aprovação.
4. `validar()` devolve **lista de problemas**, não `boolean` nem exceção.
5. Reserva é **entidade separada** do trecho, não um campo dentro dele — é o
   que permite reservar tudo de uma vez e liberar tudo de uma vez.

**Ainda em aberto:**

- `Slot` guarda a aerovia/aeroporto, ou isso fica na reserva?
- A unicidade "mesma altitude + mesma aerovia + mesmo slot" é garantida por
  `UNIQUE` no banco, por lógica no domínio, ou pelos dois?
- Controle de concorrência na aprovação (duas aprovações simultâneas no mesmo
  recurso): transação + constraint, ou lock otimista?

---

## 10. Casos de uso

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
| UC09 | Criar plano de voo | nasce `EM_EDICAO` |
| UC10 | Adicionar trecho ao percurso | só `EM_EDICAO` |
| UC11 | Remover / alterar trecho | só `EM_EDICAO` |
| UC12 | Consultar altitudes disponíveis numa aerovia por período | não reserva |
| UC13 | Consultar vagas disponíveis num aeroporto por período | não reserva |
| UC14 | **Validar plano** | não muda estado, não reserva; devolve lista de problemas |
| UC15 | **Aprovar plano** | integral: tudo ou nada |
| UC16 | Consultar plano por id | |
| UC17 | Consultar planos por situação | |
| UC18 | Abandonar plano | só `EM_EDICAO` |
| UC19 | **Cancelar plano** | só `APROVADO` com partida futura; libera reservas |

**Para a entrega parcial** (40% dos casos de uso), escolha os que demonstram o
**ciclo completo** de um plano: **UC09, UC10, UC14, UC15**, mais o mínimo de
consulta de malha (UC01, UC03) que eles exigem. Dez CRUDs fáceis não demonstram
nada.

---

# Parte 3 — Arquitetura e código

## 11. Arquitetura adotada e estrutura de pastas

**Arquitetura hexagonal (ports & adapters)**, pelos motivos da seção 6.5.

```
backend/
├── pom.xml
├── mvnw · mvnw.cmd · .mvn/              wrapper do Maven
└── src/
    ├── main/java/com/pucrs/planosdevoo/
    │   ├── PlanosDeVooApplication.java  ponto de entrada do Spring Boot
    │   ├── domain/                      NÃO conhece Spring, JPA nem infra
    │   │   ├── model/                   entidades, value objects, enums, serviços de domínio
    │   │   ├── servicos/                implementações dos casos de uso (...Service)
    │   │   └── ports/
    │   │       ├── in/                  interfaces dos casos de uso (...UseCase)
    │   │       └── out/                 interfaces dos repositórios (...Repository)
    │   └── infra/                       conhece domain; a seta é sempre para dentro
    │       ├── adapters/
    │       │   ├── in/rest/             controllers (...Controller)
    │       │   │   ├── dto/             ...Request e ...Response
    │       │   │   └── exception/       erro de domínio → status HTTP
    │       │   └── out/persistence/
    │       │       ├── entity/          entidades de banco (...Entity)
    │       │       ├── repository/      implementação das portas (Jpa...Repository)
    │       │       └── mapper/          conversão domínio ↔ persistência (...Mapper)
    │       └── config/                  beans de configuração (...Config)
    ├── main/resources/
    │   ├── application.properties
    │   └── db/{schema.sql, data.sql}
    └── test/java/com/pucrs/planosdevoo/  espelha a árvore de main
```

### 11.1 A regra de dependência

**`domain/` não importa `infra`, `org.springframework` nem
`jakarta.persistence` — nem anotação.** Se `PlanoDeVoo` tiver `@Entity`, o
isolamento acabou e o diagrama de componentes vira ficção.

A conferência é literal, e deve devolver **nada**:

```sh
grep -rn "org.springframework\|jakarta.persistence\|infra\." backend/src/main/java/com/pucrs/planosdevoo/domain/
```

Consequência aceita: a **entidade de persistência é outra classe** da entidade
de domínio, com um mapeador entre as duas. Custa duplicação de estrutura; compra
o isolamento. É a decisão que precisa ser defendida explicitamente.

> Se a equipe decidir **não** separar as duas (usar a entidade JPA como entidade
> de domínio), tudo bem — mas aí é uma arquitetura **em camadas com influência
> hexagonal**, e tem que ser documentada e chamada assim. Arquitetura
> documentada diferente da implementada é critério de avaliação explícito.

### 11.2 Onde cada coisa mora

| Pasta | O que mora aqui | O que NÃO mora aqui |
|---|---|---|
| `domain/model` | `PlanoDeVoo`, `Trecho`, `Pouso`, `Aerovia`, `Aeroporto`, `Aeronave` e subclasses, `Piloto`, `ReservaAerovia`, `ReservaVaga`, `Slot`, os enums | qualquer `import` de framework |
| `domain/servicos` | `AprovarPlanoService`, `ValidarPlanoService` — orquestração | regra que pertence à entidade |
| `domain/ports/in` | `CriarPlanoUseCase`, `AprovarPlanoUseCase`… (interfaces) | implementação |
| `domain/ports/out` | `PlanoRepository`, `AeroviaRepository`… (interfaces) | JPA, SQL |
| `infra/adapters/in/rest` | `PlanoController`, `MalhaController` | regra de negócio, consulta ao banco |
| `infra/adapters/out/persistence` | `PlanoEntity`, `JpaPlanoRepository`, `PlanoMapper` | regra de negócio |
| `infra/config` | ligação entre porta e adaptador | lógica |

### 11.3 Regras de implementação

- Controller **só traduz HTTP**: valida formato, chama o caso de uso, mapeia
  erro para status, devolve DTO.
- Regra de negócio mora na **entidade** ou no **serviço de domínio**; o caso de
  uso **orquestra**, não decide.
- Entidade de domínio **nunca** é serializada direto na resposta: sempre um DTO.
- Regra que varia por categoria de aeronave é **polimorfismo**, nunca
  `instanceof` nem `switch` por categoria.
- A aprovação de um plano é **uma** transação: ou grava todas as reservas, ou
  nenhuma.
- Injeção de dependência **por construtor**, não por campo.

### 11.4 Status HTTP

| Situação | Status |
|---|---|
| Criação bem-sucedida | 201 |
| Consulta bem-sucedida | 200 |
| Recurso inexistente | 404 (nunca `null` cru) |
| Validação reprovada | 422 (ou 400), com a **lista** de problemas |
| Conflito de reserva / operação proibida pelo estado | 409 |

### 11.5 Um desvio da referência, registrado

O estudo de caso da cadeira coloca entidades **e** serviços juntos em
`Domain/Model`. Aqui os serviços que implementam casos de uso foram separados em
`domain/servicos/`, para que `model/` fique só com o modelo. É um refinamento,
não uma mudança de estilo — mas, por ser desvio da referência, está registrado e
deve ser citado na apresentação.

---

## 12. Padrões de desenvolvimento

### 12.1 Git

Branch de integração: **`main`**.

| Elemento | Formato | Exemplo |
|---|---|---|
| **Branch** | `<tipo>/<descricao>` em kebab-case, inglês, **sem ID de task** | `feat/flight-plan-validation` |
| **Commit** | `<tipo>: <descricao>` (Conventional Commits), inglês, imperativo, minúsculo, sem ponto final | `feat: add flight plan approval use case` |
| **Título do PR** | `[TIPO] Descrição objetiva`, em português | `[FEAT] Adiciona aprovação de plano de voo` |

| Tipo | Branch / Commit | PR | Quando usar |
|---|---|---|---|
| Feature | `feat` | `[FEAT]` | caso de uso ou funcionalidade nova |
| Bugfix | `fix` | `[FIX]` | correção de defeito |
| Refactoring | `refactor` | `[REFACTOR]` | muda a estrutura sem mudar o comportamento |
| Database | `db` | `[DB]` | schema, carga inicial, índice, constraint |
| Documentation | `docs` | `[DOCS]` | diagrama, decisão, README |
| Test | `test` | `[TEST]` | só testes |
| Chore | `chore` | `[CHORE]` | build, dependência, configuração |
| Infrastructure | `infra` | `[INFRA]` | Docker, CI, ambiente |

Um commit, uma intenção. Se a mensagem precisa de "and" duas vezes, são dois
commits. Sem ID de task no nome da branch: o rastro é o **caso de uso**
(UC01–UC19) e a entrega do cronograma, citados no corpo do PR.

O histórico do Git é parte do que mostra o processo. Commita à medida que cada
caso de uso fecha — um commit único e gigante na véspera conta uma história
ruim, mesmo com o código certo.

### 12.2 Nomenclatura Java

| Elemento | Formato | Exemplo |
|---|---|---|
| Classe / interface / record | `PascalCase` | `PlanoDeVoo`, `AprovarPlanoUseCase` |
| Arquivo | `PascalCase.java`, nome da classe pública | `PlanoDeVoo.java` |
| Pacote | minúsculas, sem `_` nem hífen | `com.pucrs.planosdevoo.domain.model` |
| Método | `camelCase`, começa com verbo | `adicionarTrecho()`, `slotsOcupados()` |
| Variável e parâmetro | `camelCase`, descritivo | `velocidadeCruzeiroKmh` |
| Booleano | prefixo `is`/`has`/`can`/`should`, ou `pode`/`possui`/`esta` | `podeOperar()`, `possuiVaga()` |
| Constante | `UPPER_SNAKE_CASE` | `MARGEM_SEGURANCA` |
| Local `final` | `camelCase` (não `UPPER_SNAKE` só por ser `final`) | `final var chegada = ...` |
| Enum | `PascalCase`, valores `UPPER_SNAKE_CASE` | `SituacaoPlano.EM_EDICAO` |
| Classe de teste | `<Classe>Test` | `SlotTest.java` |
| Variável de ambiente | `UPPER_SNAKE_CASE`, sem valor sensível no código | `DATABASE_URL` |

**Sufixos da hexagonal** — o sufixo diz em que pasta a classe mora:

| Papel | Sufixo | Pacote |
|---|---|---|
| Porta de entrada | `UseCase` | `domain.ports.in` |
| Implementação do caso de uso | `Service` | `domain.servicos` |
| Porta de saída | `Repository` | `domain.ports.out` |
| Adaptador de saída | `Jpa...Repository` | `infra.adapters.out.persistence.repository` |
| Entidade de persistência | `Entity` | `...persistence.entity` |
| Mapeador | `Mapper` | `...persistence.mapper` |
| Adaptador de entrada | `Controller` | `infra.adapters.in.rest` |
| DTO de entrada / saída | `Request` / `Response` | `...rest.dto` |
| Bean de configuração | `Config` | `infra.config` |

Classe sem sufixo reconhecível em pasta de infraestrutura é sinal de que o papel
dela não foi pensado.

### 12.3 Idioma do código

| Camada | Idioma | Por quê |
|---|---|---|
| `domain/` | **português** | linguagem ubíqua do DDD e vocabulário do enunciado: `PlanoDeVoo`, `Aerovia`, `Trecho`, `Pouso` |
| `infra/` | **inglês** | nomes técnicos: `JpaPlanoRepository`, `findById`, `save` |
| Branch e commit | **inglês** | convenção |
| PR, documentação, diagramas | **português** | é o que o professor lê |

O que **não** vale: misturar dentro do mesmo pacote. `PlanoDeVoo.getFlightId()`
é o pior dos dois mundos.

### 12.4 Autorrevisão antes de commitar

Antes de cada commit, por escrito, anotando o resultado:

| # | Item | Como conferir |
|---|---|---|
| 1 | **Regra de dependência** | o `grep` da seção 11.1 devolve nada |
| 2 | **Bate com o diagrama** | classe, atributo e operação novos estão no modelo entregue? se não, atualiza o diagrama |
| 3 | **Padrão do projeto** | nomes, pacotes e idioma iguais aos dos arquivos vizinhos |
| 4 | **Reúso** | já existe método, value object ou constante que faz isso? |
| 5 | **Sem sobra** | cada classe, método e constante nova tem quem a use |
| 6 | **Sem checagem repetida** | a mesma regra validada no controller e no domínio? uma sobra |
| 7 | **Sem regra no controller** | controller só traduz HTTP |
| 8 | **Sem `instanceof` de categoria** | regra por categoria é polimorfismo |
| 9 | **Comentários** | só o porquê, na densidade dos arquivos vizinhos |
| 10 | **Build e testes** | rodados **depois** da última alteração |
| 11 | **Decisões discutíveis** | lista à parte, que vai para o registro de decisões |

**Método, não memória.** Para cada nome novo, roda a busca e anota quem usa:

```sh
grep -rn "NomeDaClasse" backend/src/
```

E relê o arquivo inteiro, não só o trecho alterado.

### 12.5 Comandos

| O que | Comando |
|---|---|
| Compilar e testar | `cd backend && ./mvnw clean test` |
| Empacotar | `cd backend && ./mvnw clean package` |
| Executar | `cd backend && ./mvnw spring-boot:run` |
| Um teste só | `cd backend && ./mvnw test -Dtest=SlotTest` |

---

## 13. Testes

"Facilidade de verificar o comportamento" é critério de avaliação explícito.

### 13.1 Prioridade

A regra mais sujeita a erro e mais barata de testar vem primeiro:

1. **Cálculo de slots** — entrada 15:52 / saída 17:07 → 3 slots. E a borda
   exata: entrada 15:00 / saída 16:00 → quantos? Decida, documente, teste.
2. **Duração e chegada** do trecho.
3. **Autonomia** com sobrevoo acumulando e pouso zerando.
4. **Altitude e janela de horário** por categoria de aeronave.
5. **Continuidade do percurso** e pouso obrigatório no destino.
6. **Mesmo dia** (chegada na data da partida).

Todas essas são testáveis **sem Spring e sem banco** — e é exatamente isso que
o isolamento do domínio compra. Se um teste de regra de negócio precisa subir a
aplicação, a arquitetura não está isolando nada.

### 13.2 Casos de uso e API

- Aprovação bem-sucedida: estado vira `APROVADO` e as reservas existem.
- **Aprovação malsucedida não deixa reserva parcial** — o enunciado cobra isso
  por escrito.
- Validação não cria reserva nenhuma.
- Cancelamento de plano futuro libera as reservas; de plano com partida passada,
  é recusado.
- Plano aprovado / cancelado / abandonado recusa modificação.

---

# Parte 4 — Dados

## 14. Banco de dados e carga inicial

### 14.1 Exigências

- Persistência **em banco de dados**. Memória ou CSV **não atende**.
- Devem persistir: malha aérea, pilotos, aeronaves, planos e reservas.
- Alterações feitas pela API **sobrevivem à reinicialização**.
- A carga inicial é **reproduzível a partir dos artefatos entregues**, sem
  nenhuma inclusão manual antes da apresentação.

A tecnologia é decisão da equipe. **H2 em arquivo** (não em memória!) é o
caminho mais curto a partir do material da cadeira; **PostgreSQL em container**
é mais próximo do real e exige um `docker-compose.yml` no repositório. Qualquer
das duas vira decisão registrada.

### 14.2 O que a carga inicial precisa ter

Já no primeiro start:

- a malha aérea inicial (4 aeroportos, 8 aerovias);
- pilotos;
- aeronaves **das três categorias**;
- um conjunto de **planos aprovados com suas reservas**.

E a restrição numérica fácil de esquecer:

> Para uma **data de referência**, pelo menos **60% das aerovias da malha
> inicial** têm alguma ocupação. Com 8 aerovias, **pelo menos 5**.

Mais três condições:

1. As ocupações são **consequência dos planos aprovados** na base — não linhas
   soltas na tabela de reservas. Reserva sem plano faz o dado mentir e a
   demonstração quebrar na primeira consulta cruzada.
2. A **data de referência** fica claramente identificada no `README.md`. É ela
   que o professor usa nos testes durante a apresentação.
3. Os dados permitem **tanto sucesso quanto conflito**: não pode deixar todos os
   recursos indisponíveis o dia inteiro, senão não dá para demonstrar aprovação
   bem-sucedida.

### 14.3 Checklist de modelagem

| # | Item | O que procurar |
|---|---|---|
| 1 | Tipo correto | data/hora como `TIMESTAMP`, nunca texto; distância e velocidade numéricos |
| 2 | Chave primária | toda tabela tem; tipo consistente |
| 3 | Integridade referencial | FK com `ON DELETE` pensado — lembrando que **aerovia nunca é excluída** |
| 4 | Unicidade | `UNIQUE` no prefixo da aeronave, no código do aeroporto, no id da aerovia |
| 5 | **Unicidade da reserva** | `UNIQUE (aerovia, altitude, slot)` e `UNIQUE (aeroporto, vaga, slot)` — última linha de defesa contra duas aprovações simultâneas |
| 6 | Índice | FK usada em filtro precisa de índice; a consulta de disponibilidade por período é a mais quente |
| 7 | Herança de aeronave | decidir a estratégia (`SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`) e justificar |
| 8 | Enum | gravado como **`STRING`**, nunca como ordinal — ordinal quebra ao reordenar o enum |
| 9 | Script de carga | roda do zero, em banco vazio, sem erro, quantas vezes for preciso |

### 14.4 Roteiro de teste da carga inicial

1. Apagar o banco por completo.
2. Subir a aplicação.
3. Conferir: 4 aeroportos, 8 aerovias, pilotos, aeronaves das 3 categorias.
4. Consultar disponibilidade na data de referência em cada uma das 8 aerovias e
   contar quantas têm ocupação — tem que dar **5 ou mais**.
5. Criar, validar e aprovar um plano pela API.
6. **Reiniciar a aplicação** e confirmar que o plano continua lá.

Esse roteiro é também o começo do roteiro da apresentação.

---

# Parte 5 — Processo

## 15. Registro do trabalho: os dois logs

Ao fim de **toda sessão de trabalho**, duas entradas:

| | `logs/log.md` | `logs/decisoes.md` |
|---|---|---|
| Registra | o que foi **feito** | o que foi **decidido** e por quê |
| Serve para | apresentação, README, relato de progresso | justificar a arquitetura, retomar de onde parou |
| Entra | commit, arquivo alterado, caso de uso concluído, verificação feita | decisão, alternativa descartada, achado, dúvida em aberto |
| Não entra | intenção, plano | entrega |

### 15.1 Regras

- **Sempre acrescentar.** Nunca reescreve nem apaga entrada antiga. Entrada
  errada é corrigida por uma nova, apontando a correção.
- **Fidelidade.** Só o que foi de fato feito. Código não commitado entra como
  pendência, não como entrega.
- **Sessão sem commit também conta.** Leitura do enunciado, desenho de diagrama,
  decisão de modelagem: registra com "Commits: nenhum".
- Decisão só entra em "Decisões" se foi **fechada**. Ideia ainda em discussão
  vai para "Em aberto".
- Toda mudança no modelo de domínio ou na estrutura de pastas gera entrada em
  `decisoes.md`, porque muda um artefato já entregue.

### 15.2 Formato do `log.md`

```markdown
## AAAA-MM-DD — título curto

**Resumo.** O que foi feito, em 2 a 5 linhas.

**Casos de uso tocados.** UCnn (nome) — estado.

**Arquivos tocados.**
- `caminho/arquivo` — o que mudou

**Verificações feitas.**
- `./mvnw clean test` — N testes, resultado
- requisições manuais: quais e o resultado
- o que NÃO foi testado

**Commits.**
- `hash` — mensagem

**Pendências.**
- O que ficou aberto e o próximo passo
```

### 15.3 Formato do `decisoes.md`

```markdown
## AAAA-MM-DD — título curto

**Assunto.** Do que se tratou, em 1 ou 2 linhas.

**Decisões.**
- O que ficou decidido e por quê. Alternativa descartada e o motivo.

**Em aberto.**
- Pergunta sem resposta, e do que ela depende.

**Achados técnicos.**
- Fato verificado, com a referência.

**Retomar por aqui.**
- O próximo passo, escrito para ser lido dias depois sem contexto.
```

O campo mais valioso é **"Decisões" com a alternativa descartada**. É ele que
responde o professor quando pergunta "por que não fez do outro jeito?", e é ele
que vira o registro formal de decisão arquitetural.

### 15.4 Início e fim de sessão

No início: ler o "Retomar por aqui" da última entrada; depois conferir o estado
real **antes** de planejar (`git status`, `git log --oneline -5`).

No fim: conferir mudança sem commit, escrever as duas entradas, anotar o
próximo passo.

---

# Parte 6 — Entrega

## 16. Cronograma e artefatos exigidos

### 16.1 Cronograma

| Data | Entrega |
|---|---|
| **05/10** | Diagrama de domínio · Casos de uso identificados |
| **07/10** | Diagramas C4 (Contexto, Containers, Componentes) · Estrutura de pastas |
| 12/10 | Feriado |
| **14/10** | Implementação de **40% dos casos de uso** |
| **19/10** | **Apresentação da solução completa** |
| 21/10 | Semana acadêmica |

O que isso impõe na ordem de trabalho:

1. **Domínio primeiro.** O diagrama de 05/10 é o contrato. O código depois tem
   que bater com ele — se mudar, o diagrama muda junto, e a mudança é
   registrada.
2. **Estrutura de pastas antes do código.** A entrega de 07/10 é a árvore de
   pacotes criada no repositório, não só desenhada. Criar pasta depois, conforme
   dá vontade, é como a arquitetura documentada deixa de bater com a
   implementada.
3. **40% em 14/10** exige escolher quais casos de uso — os que demonstram o
   ciclo completo (seção 10).
4. **19/10 é demonstração ao vivo**, com requisições que o professor pode pedir
   na hora. Dados iniciais e data de referência prontos bem antes disso.

### 16.2 Documentação C4

| Nível | O que mostra | Cuidado |
|---|---|---|
| **1 — Contexto** | o sistema, os usuários e os sistemas externos | o cliente REST aparece como ator, embora não seja escopo de implementação |
| **2 — Containers** | elementos executáveis e armazenadores: aplicação Spring Boot e banco; responsabilidades, tecnologias, comunicação | tecnologia e protocolo escritos em cada container e em cada seta |
| **3 — Componentes** | organização interna da aplicação: adaptadores de entrada, casos de uso, modelo, portas, adaptadores de saída | é aqui que a hexagonal aparece, com as setas sempre para dentro |

Nível 4 (código) **não** é exigido — o modelo de domínio em UML cobre isso.

Os diagramas representam a arquitetura **efetivamente adotada**. Diagrama que
mostra uma porta que o código não tem é erro de entrega, não detalhe.

Ferramenta sugerida: **PlantUML** (`.puml` versionado + `.png` exportado),
porque o fonte fica no Git e o diff mostra o que mudou. Structurizr também
serve.

### 16.3 Registro de decisões arquiteturais

O enunciado pede, para cada decisão relevante: **a decisão adotada, as
principais alternativas consideradas e a justificativa**. Não exige formato. Um
arquivo por decisão:

```markdown
# ADR NNNN — Título curto da decisão

**Data:** AAAA-MM-DD
**Situação:** proposta | aceita | substituída pela ADR NNNN

## Contexto
O que no problema motivou a decisão. Cita o requisito do enunciado em jogo.

## Alternativas consideradas
1. <alternativa> — vantagem; desvantagem; por que não foi escolhida.
2. ...

## Decisão
O que foi decidido, em uma ou duas frases.

## Consequências
O que facilita, o que custa, e o que passa a ser obrigatório por causa disso.
```

As decisões que este trabalho precisa ter registradas:

1. Estilo arquitetural (hexagonal × camadas × outro).
2. Entidade de domínio separada da entidade de persistência — ou não.
3. Banco escolhido, e por que não em memória.
4. Como a atomicidade da aprovação é garantida.
5. Como o cálculo de slots foi modelado.
6. Como categoria nova de aeronave entra sem alterar a validação.
7. Estratégia de herança da hierarquia de aeronaves no banco.
8. Idioma do código (domínio em português).

Cada uma nasce de uma entrada em `logs/decisoes.md`; o ADR é a versão limpa.

### 16.4 O `README.md`

Precisa explicar: como preparar o ambiente; como inicializar o banco; como
carregar os dados iniciais; como compilar e executar; como acessar a API; **qual
é a data de referência**; e onde estão o modelo de domínio, os C4 e as decisões.

Critério: alguém que nunca viu o projeto clona e roda **sem perguntar nada**.

### 16.5 OpenAPI

Exigida documentação das operações, parâmetros, requisições e respostas, por
OpenAPI ou equivalente. Com Spring Boot, `springdoc-openapi` resolve — e o
Swagger UI serve como ferramenta de demonstração na apresentação.

Documente também os **casos de erro**, não só o caminho feliz: a qualidade da
API "inclusive em situações de sucesso e erro" está na lista de critérios.

---

## 17. Apresentação

A demonstração é feita com **requisições HTTP à API**, usando qualquer
ferramenta (cliente HTTP, linha de comando, Swagger UI). Usa a **data de
referência** e os dados iniciais, e precisa cobrir:

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

Guarde cada uma dessas requisições num arquivo versionado (`requests.http` ou
coleção equivalente), numerado e comentado.

**O professor pode pedir requisições fora do roteiro.** Por isso a API precisa
ser navegável e os dados precisam ter folga — não só os casos ensaiados.

---

# Parte 7 — Consulta rápida

## 18. Armadilhas do enunciado

As que custam nota e são fáceis de deixar passar:

| # | Armadilha | O que fazer |
|---|---|---|
| 1 | Tratar aerovia como editável | imutável após cadastro: sem `PUT`, sem `DELETE`, sem setter |
| 2 | Aerovia como ligação bidirecional | é **orientada**; POA→FLN e FLN→POA são registros distintos (R1 e R8) |
| 3 | Deixar o usuário informar o horário de chegada | é **calculado**; horário informado que não bate é erro |
| 4 | Esquecer que uso parcial ocupa o slot **inteiro** | 15:52–17:07 são 3 slots, não 1 |
| 5 | Sobrevoo zerando a autonomia | só **pouso** reabastece; sobrevoo acumula |
| 6 | Usar 100% da autonomia | o limite é **85%** |
| 7 | Esquecer o pouso obrigatório no destino final | e ele também informa vaga e permanência |
| 8 | Validar reservando | validar **não** reserva e **não** muda estado |
| 9 | Aprovação deixando reserva parcial | tudo ou nada, numa transação |
| 10 | Cancelar plano com partida passada | só plano **futuro** é cancelável |
| 11 | Permitir alterar plano aprovado/cancelado/abandonado | só `EM_EDICAO` é modificável |
| 12 | Voo terminando no dia seguinte | chegada ao destino final **na data da partida**; só a permanência final pode virar o dia |
| 13 | Malha aérea em constante no código | vem do banco; aeroporto e aerovia novos entram sem recompilar |
| 14 | `if` por categoria de aeronave espalhado | polimorfismo; categoria nova é o teste de extensão |
| 15 | Menos de 5 aerovias ocupadas na data de referência | o mínimo é 60% de 8 |
| 16 | Reserva inserida direto no seed, sem plano | ocupação é **consequência de plano aprovado** |
| 17 | Banco em memória | o estado tem que sobreviver à reinicialização |
| 18 | Diagrama divergente do código | são avaliados **juntos** |
| 19 | `@Entity` numa classe de `domain/` | quebra a regra de dependência e invalida o diagrama de componentes |
| 20 | Enum gravado como ordinal | grave como `STRING`; ordinal quebra ao reordenar o enum |

---

## 19. Checklists

**Começar no projeto**

- [ ] Li este arquivo inteiro, inclusive a Parte 1.
- [ ] Rodei `./mvnw clean test` em `backend/` e passou.
- [ ] Sei qual é a regra de dependência e como conferi-la (seção 11.1).
- [ ] Sei onde escrever os logs e em que formato (seção 15).

**Entrega do diagrama de domínio**

- [ ] Modelo em UML, com atributos, operações e cardinalidades.
- [ ] Value objects e raiz de agregado identificados.
- [ ] Regras do enunciado mapeadas para onde moram no modelo.
- [ ] Casos de uso numerados, com ator, pré-condição e resultado.
- [ ] Fonte (`.puml`) e imagem versionados.
- [ ] Decisões de modelagem registradas.

**Entrega da arquitetura**

- [ ] C4 níveis 1, 2 e 3, com tecnologias nas setas.
- [ ] Estrutura de pastas criada no repositório, não só desenhada.
- [ ] Regra de dependência escrita e respeitada.
- [ ] Decisão do estilo arquitetural registrada, com alternativas.

**Entrega parcial da implementação**

- [ ] Ciclo completo de um plano funcionando pela API.
- [ ] Banco persistindo entre reinicializações.
- [ ] Carga inicial rodando do zero.
- [ ] Testes das regras de cálculo passando.
- [ ] Quais casos de uso estão prontos, listados no log.

**Antes de cada commit**

- [ ] Autorrevisão da seção 12.4 feita.
- [ ] `./mvnw clean test` limpo depois da última alteração.
- [ ] `git status` mostra só os arquivos da mudança.
- [ ] Mensagem em Conventional Commits.
- [ ] Diagrama atualizado, se a mudança mexeu no domínio.

**Antes de abrir o PR**

- [ ] Branch atualizada com `main`, sem conflito.
- [ ] `./mvnw clean test` e `./mvnw clean package` passando.
- [ ] Caso(s) de uso e entrega do cronograma citados no corpo.
- [ ] O que **não** foi testado, dito explicitamente.
- [ ] Entradas nos dois logs feitas.

**Fim de sessão**

- [ ] Nada sem commit que eu não saiba.
- [ ] Entrada no `log.md`.
- [ ] Entrada no `decisoes.md`, se houve decisão.
- [ ] "Retomar por aqui" escrito.

**Antes da apresentação**

- [ ] Banco apagado e recriado do zero, com sucesso.
- [ ] As 17 demonstrações da seção 17 testadas, em ordem.
- [ ] Data de referência conferida: 5+ aerovias com ocupação.
- [ ] Roteiro de requisições completo e numerado.
- [ ] Swagger UI abrindo.
- [ ] README seguido por alguém de fora, do clone à primeira requisição.
- [ ] Diagramas conferidos contra o código, um por um.
- [ ] As 8 decisões arquiteturais registradas.

---

## 20. Glossário

Vocabulário do domínio — use estes termos no código, nos diagramas e nas
conversas. Trocar de palavra no meio do caminho é o começo da perda de
integridade conceitual.

| Termo | Significado |
|---|---|
| **Aerovia** | ligação **orientada** entre dois aeroportos, com distância em km e id único. Imutável após o cadastro |
| **Malha aérea** | o conjunto de aeroportos e aerovias cadastrados |
| **Percurso** | sequência ordenada de trechos que leva da origem ao destino do plano |
| **Trecho** | o uso de uma aerovia: altitude, horário de entrada e ação no destino |
| **Sobrevoo** | passar pelo aeroporto de destino do trecho sem pousar. Não usa vaga, não reabastece, acumula distância |
| **Pouso** | parar no aeroporto. Usa vaga durante a permanência e reabastece |
| **Permanência** | tempo, em minutos, que a aeronave fica em solo após um pouso |
| **Vaga** | posição de estacionamento num aeroporto. Qualquer vaga serve para qualquer aeronave |
| **Slot** | faixa de uma hora alinhada à hora cheia (`15:00–16:00`). Unidade de controle de ocupação; uso parcial ocupa o slot inteiro |
| **Altitude** | nível de voo, de 25.000 a 35.000 pés, de 1.000 em 1.000 |
| **Autonomia** | distância máxima desde o último abastecimento. Usável só até 85% do nominal |
| **Velocidade de cruzeiro** | velocidade constante usada para calcular a duração de cada trecho |
| **Plano de voo** | o agregado central: piloto, aeronave, origem, destino, partida, percurso e situação |
| **Validar** | conferir as regras sem alterar estado e sem reservar |
| **Aprovar** | validar e **reservar tudo**, de forma integral, mudando o estado para aprovado |
| **Reserva** | ocupação efetivada de uma altitude numa aerovia, ou de uma vaga num aeroporto, em slots específicos |
| **Data de referência** | a data da carga inicial em que pelo menos 5 das 8 aerovias têm ocupação; usada na apresentação |
| **Porta** | interface definida pelo domínio. De **entrada** (caso de uso) ou de **saída** (repositório) |
| **Adaptador** | classe que liga uma tecnologia a uma porta. De **entrada** (REST) ou de **saída** (persistência) |
| **Caso de uso** | uma operação que o sistema oferece, do ponto de vista de quem o usa |
| **Agregado** | conjunto de objetos tratado como unidade de consistência, com uma raiz como única porta de entrada |
| **Value object** | objeto sem identidade, definido pelos seus valores e imutável |
| **Linguagem ubíqua** | o vocabulário único e compartilhado entre enunciado, diagrama, código e conversa |
