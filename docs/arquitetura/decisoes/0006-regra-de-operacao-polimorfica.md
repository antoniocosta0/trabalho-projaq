# ADR 0006 — Regra de operação polimórfica na hierarquia de aeronave

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

Cada categoria de aeronave tem a sua própria regra de altitude e de janela de
horário (§3 do enunciado): particular só opera entre 25.000 e 27.000 pés;
comercial de passageiros exige 28.000 pés ou mais; comercial de carga pode usar
qualquer altitude prevista, mas todo o uso da aerovia precisa ocorrer entre
00:00, inclusive, e 06:00, exclusive.

O enunciado avisa em seguida que **"novas categorias e novas regras podem
aparecer durante o desenvolvimento"**, e o critério de avaliação fala em
"capacidade de acomodar as evoluções previstas" (§8.4). Na prática isso
significa que o professor pode, durante a apresentação, pedir uma categoria
nova — e a pergunta é quantos arquivos existentes precisam mudar para ela
entrar.

## Alternativas consideradas

1. **`if` ou `switch` por categoria dentro da validação do plano.** Vantagem:
   todas as regras visíveis num lugar só, fácil de ler a comparação entre
   categorias. Desvantagem: categoria nova obriga a alterar o serviço de
   validação, que é código testado e compartilhado — exatamente o oposto de
   aberto para extensão. Não foi escolhida; é o anti-padrão que o enunciado
   está cobrando que se evite.

2. **Strategy como objeto separado, injetado na aeronave.** Vantagem:
   permitiria trocar a regra de uma aeronave em tempo de execução, e testar a
   regra sem construir a aeronave. Desvantagem: aqui a regra não varia por
   aeronave, varia por categoria — e categoria é justamente o que a subclasse
   já representa. A indireção extra não compra nada. Não foi escolhida por
   excesso de cerimônia para o problema.

3. **Tabela de regras no banco, interpretada em tempo de execução.** Vantagem:
   categoria nova entraria sem recompilar. Desvantagem: as regras não são
   homogêneas — uma é conjunto de altitudes, outra é faixa, outra é janela de
   horário — e caberiam mal num esquema genérico. Não foi escolhida por
   complexidade desproporcional.

## Decisão

`Aeronave` é **abstrata** e declara
`podeOperar(altitude, entrada, saida) : boolean`. Cada categoria é uma subclasse
concreta que implementa a sua própria regra: `AeronaveParticular`,
`AeronavePassageiros` e `AeronaveCarga`. A validação do plano chama
`podeOperar` sem saber qual categoria está tratando.

A margem de autonomia, que é igual para todas, fica na classe base, em
`distanciaMaxSemAbastecer()`.

## Consequências

**Facilita.** Categoria nova é **uma classe nova**, sem alterar nenhuma linha
da validação do plano. A regra de cada categoria fica junto dos dados dela e
pode ser testada isoladamente.

**Custa.** As regras ficam espalhadas por três arquivos em vez de concentradas
num; comparar categorias exige abrir as três subclasses.

**Passa a ser obrigatório.**

- Nenhum `instanceof` nem `switch` por categoria fora do ponto de construção.
  O PR cobra isso como item de checklist.
- Existe **um** lugar que conhece os nomes das categorias: a
  `FabricaDeAeronaves`, em `domain/model`. É a fronteira de criação, e é
  inevitável — algum ponto do sistema precisa decidir qual objeto instanciar a
  partir de um dado. Categoria nova, portanto, custa: uma subclasse nova no
  domínio, um `case` novo na fábrica, e nada mais.

  Essa fronteira **começou no `FrotaMapper`**, em `infra`, quando a única forma
  de uma aeronave entrar no sistema era vindo do banco. Com o `POST /aeronaves`
  (PR #17) passaram a existir dois caminhos de criação, e manter a escolha no
  mapeador significaria ou duplicar o `switch`, ou fazer o cadastro depender da
  persistência para construir uma entidade de domínio. A fábrica foi movida para
  o domínio e o mapeador passou a consumi-la: o conhecimento sobre as subclasses
  volta para onde elas moram, e a regra de dependência da ADR 0001 fica
  respeitada nos dois caminhos.
- A hierarquia é plana, sem `AeronaveComercial` intermediária: `companhiaAerea`
  aparece repetido em `AeronaveCarga` e `AeronavePassageiros`. Um nível a mais
  sem comportamento próprio não se pagaria.
