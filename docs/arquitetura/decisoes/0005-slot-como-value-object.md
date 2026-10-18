# ADR 0005 — O slot de ocupação como value object

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

O enunciado controla a ocupação de aerovias e de vagas em **slots de uma hora
alinhados à hora cheia**, e estabelece que qualquer uso parcial ocupa o slot
inteiro (§5.1 e §5.2). O exemplo do próprio enunciado: uma aerovia usada entre
15:52 e 17:07 ocupa três slots — 15:00–16:00, 16:00–17:00 e 17:00–18:00.
Conflito é a coincidência em **pelo menos um** slot.

Essa conversão de "intervalo de tempo" para "lista de slots" é usada em quatro
lugares diferentes: ao verificar disponibilidade de altitude, ao verificar
disponibilidade de vaga, ao criar as reservas na aprovação e ao liberá-las no
cancelamento. É também a regra mais fácil de errar por um slot a mais ou a
menos — e um erro aqui não aparece como exceção, aparece como reserva
silenciosamente errada.

O enunciado deixa um caso em aberto: um uso que termina **exatamente** na hora
cheia ocupa o slot seguinte? Entrada 15:00 e saída 16:00 são um slot ou dois?

## Alternativas consideradas

1. **Calcular os slots no ponto de uso.** Vantagem: nenhuma classe nova.
   Desvantagem: a mesma aritmética de arredondamento replicada em quatro
   lugares, com quatro chances de divergir — e nenhum ponto único para testar.
   Não foi escolhida.

2. **Guardar o intervalo (início e fim) na reserva e detectar conflito por
   sobreposição de intervalos.** Vantagem: menos linhas no banco — uma reserva
   por trecho em vez de uma por hora. Desvantagem: a detecção de conflito vira
   uma comparação de intervalos em SQL, o que inviabiliza a restrição `UNIQUE`
   como defesa contra aprovações simultâneas (ADR 0004). Não foi escolhida
   porque joga fora a garantia mais barata de integridade que o banco oferece.

3. **`Slot` como entidade, com identidade própria no banco.** Desvantagem: um
   slot das 15:00 é intercambiável com qualquer outro slot das 15:00 — não faz
   sentido perguntar "qual dos dois?". Dar identidade a ele seria modelar mal.
   Não foi escolhida.

## Decisão

`Slot` é um **value object** do domínio, com um único atributo `inicio`
normalizado para a hora cheia no construtor, `equals`/`hashCode` por valor, e a
operação estática `Slot.entre(inicio, fim) : List<Slot>`.

O caso em aberto foi resolvido como **intervalo semiaberto**: o slot final só é
ocupado se o uso efetivamente entrar nele. Entrada 15:00 e saída 16:00 ocupam
**um** slot; 15:52 a 17:07 ocupam três.

## Consequências

**Facilita.** A regra tem um ponto único de verdade e é testável isoladamente,
sem Spring e sem banco — `SlotTest` cobre o exemplo do enunciado, as duas
bordas exatas e os casos inválidos. `Trecho.slotsOcupados()` e as reservas
apenas consomem essa operação.

**Custa.** Uma linha por hora na tabela de reserva, em vez de uma por trecho.
Para os voos deste domínio, que duram poucas horas, é irrelevante.

**Passa a ser obrigatório.**

- A granularidade da reserva é o slot: uma `ReservaAerovia` por slot ocupado e
  uma `ReservaVaga` por slot de permanência.
- As restrições `UNIQUE (aerovia_id, altitude, slot_inicio)` e
  `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)` dependem desta decisão
  para existir.
- A escolha do intervalo semiaberto precisa estar refletida nos testes e no
  modelo de domínio, e qualquer mudança nela quebra reservas já gravadas.
