# ADR 0010 — Interpretações do enunciado adotadas pelo código

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

O enunciado define as regras com precisão, mas, como todo texto, deixa bordas
sem resposta explícita. Em cada uma delas o código precisou decidir — e uma
decisão de borda tomada em silêncio é indistinguível de um descuido para quem
lê de fora.

Esta ADR reúne as interpretações que não têm ADR própria. Nenhuma delas muda a
arquitetura; todas mudam o comportamento observável na apresentação, e é
previsível que apareçam como pergunta.

## Alternativas consideradas

A alternativa geral — e a que foi descartada — era **não registrar**, deixando
cada escolha implícita no código e nos testes. Ela foi descartada porque o §8.4
avalia a justificativa das decisões, e porque uma interpretação não registrada
vira improviso quando perguntada na banca. O custo de registrar é este arquivo.

Para cada item abaixo, a alternativa é sempre a leitura oposta, descrita junto.

## Decisão

### 1. Intervalo semiaberto `[início, fim)` nos slots

Um uso que termina **exatamente** na hora cheia não ocupa o slot seguinte:
entrada 15:00 e saída 16:00 ocupam **um** slot, não dois. O exemplo do
enunciado (15:52 a 17:07 ocupando três slots) é compatível com as duas leituras;
esta é a que não cobra do usuário um slot que ele não usou. Detalhada na ADR
0005.

### 2. A janela da aeronave de carga fecha do mesmo jeito

O §3 diz que a aeronave de carga opera entre 00:00 **inclusive** e 06:00
**exclusive**. Um voo que termina exatamente às 06:00 é **aceito**, pela mesma
regra de borda do item 1: às 06:00 a aerovia já está livre.

Alternativa descartada: recusar, pela leitura literal de "exclusive". Ela
tornaria a borda da janela diferente da borda do slot, e duas regras de borda
opostas no mesmo sistema é o tipo de inconsistência que gera bug.

### 3. Duração arredondada ao minuto

`Aerovia.duracao()` arredonda para o minuto mais próximo. A divisão
distância ÷ velocidade quase nunca dá um número redondo: 363 km a 830 km/h dá
26 minutos e 14 segundos.

Sem arredondar, **nenhum horário digitado pelo usuário bateria** com a chegada
calculada, e a validação de horários — que o enunciado cobra — rejeitaria todo
plano. Alternativa descartada: manter os segundos e comparar com tolerância;
seria uma tolerância arbitrária espalhada por toda comparação de horário.

### 4. Aprovar um voo que já partiu é recusado

O enunciado proíbe explicitamente **cancelar** um plano cuja partida ocorreu
(§6.8), e não diz nada sobre aprovar. O código recusa os dois.

Alternativa descartada: permitir, pela letra do texto. Ela foi o comportamento
inicial, e caiu porque aprovar um voo que já decolou não se defende em
apresentação: a aprovação existe para autorizar um voo futuro. Como a
demonstração de "cancelamento recusado" dependia de aprovar um plano no
passado, a carga inicial ganhou o plano 1005, **já aprovado**, partindo antes da
data de referência.

### 5. O "agora" é um `Clock` injetado

Nenhum ponto do sistema chama `LocalDateTime.now()` direto. O relógio é um bean,
configurável por `app.relogio.zona` e `app.relogio.instante-fixo`.

Sem isso, as regras que dependem da hora só poderiam ser testadas com datas
distantes no futuro, e a apresentação só poderia ser ensaiada no próprio dia.
Alternativa descartada: usar o relógio do sistema e escolher datas longe o
bastante para o teste não quebrar — o que apenas adia a quebra.

### 6. Ids da carga inicial a partir de 1001

Os planos do `data.sql` usam ids de 1001 em diante, e não 1 a 5. A identidade do
H2 não avança quando o id é informado, então os planos criados pela API durante
a demonstração recebem 1, 2, 3… — que é o que o roteiro
`docs/api/requests.http` espera.

Alternativa descartada: numerar o seed a partir de 1 e o roteiro a partir de 6.
Ficaria frágil: qualquer plano a mais ou a menos na carga inicial quebraria
todos os blocos seguintes do roteiro.

### 7. A vaga de origem não é reservada

O enunciado trata a vaga como recurso do **pouso** (§4.2 e §4.3). O avião que
parte do aeroporto de origem não reserva vaga lá. Alternativa descartada:
reservar também na origem — o enunciado não pede, e inventaria ocupação que ele
não previu.

## Consequências

**Facilita.** Cada uma destas respostas está escrita antes da pergunta, com a
alternativa que foi descartada e o motivo. São as perguntas mais prováveis da
banca sobre comportamento de borda.

**Custa.** Esta ADR precisa ser revisitada se qualquer uma das bordas mudar —
ela é, por natureza, a mais sujeita a ficar defasada, porque não descreve uma
estrutura, e sim um conjunto de escolhas pontuais.

**Passa a ser obrigatório.**

- Toda borda decidida sem respaldo literal no enunciado entra aqui, com a
  alternativa descartada.
- Os itens 1, 2, 3 e 4 têm teste automatizado que falha se o comportamento
  mudar: `SlotTest`, `PlanoDeVooTest`, `TrechoTest` e `ReservasDoPlanoTest`.
- O item 4 tem, além do teste de domínio, um teste que falha se o plano com
  partida passada sumir da carga inicial (`SeedConsistenciaTest`), porque sem
  ele a demonstração volta a depender do relógio.
