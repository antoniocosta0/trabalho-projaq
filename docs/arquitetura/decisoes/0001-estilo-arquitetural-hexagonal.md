# ADR 0001 — Arquitetura hexagonal (ports & adapters)

**Data:** 2026-10-15
**Situação:** aceita

## Contexto

O enunciado não exige estilo arquitetural específico, mas cobra que a equipe
**explique e justifique suas decisões** (§8.4) e avalia a "adequação do estilo
arquitetural escolhido", a "separação e distribuição de responsabilidades" e a
"coerência entre domínio, arquitetura, documentação e implementação".

Três requisitos empurram a decisão para um estilo que isole o domínio:

1. **Evoluções previstas.** O enunciado avisa que "novas categorias e novas
   regras podem aparecer durante o desenvolvimento" (§3) e que novos
   aeroportos e aerovias podem ser informados pelo professor, "sem alterar a
   lógica da aplicação" (§2). O critério de avaliação chama isso de
   "capacidade de acomodar as evoluções previstas".
2. **Regras de negócio densas e testáveis.** Cálculo de slots, duração de
   trecho, autonomia com margem de 15%, janelas por categoria de aeronave. São
   regras puras, que não deveriam precisar de banco nem de HTTP para serem
   verificadas.
3. **Verificabilidade.** "Facilidade de verificar o comportamento pela API" é
   critério de avaliação, e o professor vai pedir requisições fora do roteiro.

Some-se a isso que a referência mais forte do material da cadeira é o
`P7a_EstudoDeCasoArquiteturaHexagonal`, cujo objetivo declarado é "isolar as
classes de domínio das tecnologias necessárias para interagir com o mundo
exterior".

## Alternativas consideradas

1. **Camadas clássicas (controller → service → repository).** Vantagem: é o
   caminho padrão do Spring Boot, todo mundo do grupo já escreveu assim, e sai
   mais rápido. Desvantagem: a dependência aponta para fora — o serviço conhece
   o repositório JPA, e o domínio acaba conhecendo a tecnologia de persistência.
   Na prática as entidades viram `@Entity` e o teste de uma regra de negócio
   passa a exigir contexto Spring. Não foi escolhida porque joga fora
   justamente o que o trabalho mais cobra: isolamento do domínio e testes de
   regra sem infraestrutura.

2. **Transaction Script / Service Layer anêmico.** Vantagem: simples e direto
   para CRUD. Desvantagem: as regras migram das entidades para serviços
   procedurais, e `Aeronave` vira um saco de getters com a regra de categoria
   espalhada em `if`s pelo serviço. Não foi escolhida porque o enunciado
   explicitamente testa extensão por categoria nova, e esse estilo torna cada
   categoria nova uma alteração em código existente.

3. **Hexagonal com a entidade JPA servindo de entidade de domínio.** Vantagem:
   mantém o desenho de portas e adaptadores sem o custo de duplicar classes e
   escrever mapeadores. Desvantagem: `jakarta.persistence` entra no domínio, e
   o isolamento vira nominal. Não foi escolhida; o assunto é tratado à parte na
   ADR 0002.

4. **Microsserviços.** Descartada sem discussão longa: é um backend de trabalho
   acadêmico com um único contexto delimitado, entregue por cinco pessoas em
   poucas semanas. O custo operacional não tem contrapartida.

## Decisão

A aplicação é organizada em **arquitetura hexagonal (ports & adapters)**, com
dois pacotes no primeiro nível — `domain` e `infra` — e a regra de dependência
apontando sempre para dentro:

```
domain/
├── model/              entidades, value objects, enums
└── ports/
    ├── in/usecases/    casos de uso (portas de entrada)
    └── out/repositories/ interfaces de repositório (portas de saída)
infra/
├── adapters/
│   ├── in/rest/        controllers e DTOs
│   └── out/persistence/ entidades JPA, Spring Data, mapeadores
└── config/             composição dos beans
```

`domain` não importa `infra`, `org.springframework` nem `jakarta.persistence`
— nem anotação.

## Consequências

**As portas de entrada são classes concretas, não interfaces.** `AprovarPlanoUC`
é a própria classe, em `domain/ports/in/usecases`, e não uma interface
`AprovarPlanoUseCase` com um `AprovarPlanoService` que a implementa. É o que o
estudo de caso da cadeira faz, e evita uma interface com um único implementador
— abstração que não abstrai nada e dobra o número de arquivos. O custo: trocar a
implementação de um caso de uso exige editar a classe, não apontar o bean para
outra. Para este sistema, onde não há duas implementações de caso de uso nem
previsão de haver, o custo não se realiza.

**Facilita.** Toda regra de negócio é testável sem subir o Spring e sem banco:
`SlotTest`, `TrechoTest` e os testes de validação do plano rodam como JUnit
puro. Trocar JPA por JDBC, ou H2 por PostgreSQL, não toca em nenhuma classe de
domínio. Categoria nova de aeronave entra como subclasse, sem alterar a
validação (ADR 0006).

**Custa.** Mais classes e mais indireção do que camadas puras: cada acesso a
dado passa por uma interface no domínio e uma implementação na infraestrutura,
e as entidades de persistência são classes separadas (ADR 0002). Para os quatro
CRUDs de cadastro, esse custo é puro overhead — é o preço de uniformidade.

**Passa a ser obrigatório.**

- O PR cobra, como item de checklist, que
  `grep -rn "org.springframework\|jakarta.persistence\|infra\." backend/src/main/java/com/pucrs/planosdevoo/domain/`
  não devolva nada.
- Caso de uso novo exige porta em `domain/ports/in`; acesso a dado novo exige
  porta em `domain/ports/out` com implementação em
  `infra/adapters/out/persistence`.
- Os casos de uso não levam anotação do Spring: viram beans em
  `infra/config/UseCaseConfig`, por construtor. Isso diverge do exemplo da
  cadeira, que usa `@Component` nos casos de uso, e é uma escolha consciente
  para manter `domain` sem nenhum import de framework.
- O diagrama C4 de nível 3 precisa mostrar esta decomposição, com as setas para
  dentro, e ser atualizado no mesmo PR que mudar a estrutura.
