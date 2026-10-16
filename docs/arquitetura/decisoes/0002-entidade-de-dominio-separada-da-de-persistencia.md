# ADR 0002 — Entidade de domínio separada da entidade de persistência

**Data:** 2026-10-15
**Situação:** aceita

## Contexto

A ADR 0001 adotou arquitetura hexagonal, cuja regra central é que o domínio não
conheça tecnologia. Resta decidir o caso mais concreto e mais caro dessa regra:
a classe `PlanoDeVoo` que carrega as regras de negócio é a **mesma** classe que
o Hibernate mapeia para a tabela `plano_de_voo`, ou são duas?

A pergunta não é acadêmica. Se forem a mesma, `@Entity`, `@Id`,
`@ManyToOne` e `@Column` entram em `domain/model`, e com eles vêm as exigências
do JPA: construtor sem argumentos, campos não-finais, getters e setters para o
provedor. Isso conflita diretamente com o desenho do domínio — `Aerovia` é
imutável após o cadastro (§2 do enunciado) e `Slot` e `Pouso` são value objects
comparados por valor.

O critério de avaliação em jogo é "coerência entre domínio, arquitetura,
documentação e implementação": um C4 que mostra o domínio isolado enquanto o
código tem `jakarta.persistence` dentro dele é documentação que não corresponde
à implementação.

## Alternativas consideradas

1. **Uma classe só, anotada com JPA.** Vantagem: metade das classes, nenhum
   mapeador para escrever e manter, nenhuma chance de os dois lados saírem de
   sincronia. É sensivelmente mais rápido de entregar. Desvantagem: o
   isolamento do domínio deixa de existir de fato; `Aerovia` precisa abrir mão
   da imutabilidade para satisfazer o provedor; e o diagrama de componentes
   passa a mentir. Não foi escolhida — mas, se tivesse sido, a arquitetura
   teria que ser documentada como "camadas com influência hexagonal", não como
   hexagonal.

2. **Uma classe só, com o mapeamento em XML (`orm.xml`) em vez de anotações.**
   Vantagem: tira as anotações do domínio mantendo uma classe só. Desvantagem:
   o acoplamento continua, só fica invisível — o domínio segue tendo que
   obedecer às restrições estruturais do JPA, e agora há um arquivo XML
   distante do código que ninguém lembra de atualizar. Não foi escolhida por
   piorar a manutenibilidade sem resolver o problema real.

3. **Duas classes, com mapeamento automático por biblioteca (MapStruct).**
   Vantagem: elimina o mapeador escrito à mão. Desvantagem: mais uma
   dependência e mais uma ferramenta para o grupo aprender a três dias da
   entrega, e os mapeamentos aqui são pequenos. Não foi escolhida por custo de
   adoção, não por mérito.

## Decisão

São **duas classes separadas**. `domain/model/PlanoDeVoo` carrega as regras e
não tem nenhuma anotação; `infra/adapters/out/persistence/PlanoDeVooJpaEntity`
carrega o mapeamento. A conversão fica em mapeadores explícitos por assunto —
`MalhaAereaMapper`, `FrotaMapper`, `PlanoDeVooMapper` — usados apenas pelos
adaptadores de saída.

## Consequências

**Facilita.** O domínio fica livre para ser modelado pelo problema e não pelo
provedor de persistência: `Aerovia` é imutável de verdade, com campos `final` e
sem setter; `Slot` e `Pouso` são value objects; `PlanoDeVoo` controla suas
transições de estado sem que o Hibernate precise de um construtor vazio.
Trocar JPA por JDBC, se a decisão mudar, altera só os adaptadores de saída.

**Custa.** Duplicação de estrutura. Cada atributo novo em uma entidade de
domínio exige o atributo correspondente na entidade JPA e as duas linhas no
mapeador. Para `Trecho`, `Pouso` e as duas reservas, que ainda vão ser
escritos, são seis classes a mais do que seriam com a alternativa 1. É o custo
que esta decisão assume conscientemente.

**Passa a ser obrigatório.**

- Toda entidade de domínio nova nasce com entidade JPA e mapeador
  correspondentes. O PR cobra isso.
- Nenhuma entidade de domínio é serializada direto pela API: a saída é sempre
  um `...ResponseDTO`. São, portanto, três representações do mesmo conceito
  (domínio, persistência, API) — o que é intencional, porque cada uma muda por
  uma razão diferente.
- O mapeador é o único lugar que conhece as duas formas; nenhum caso de uso
  pode importar uma entidade JPA.
