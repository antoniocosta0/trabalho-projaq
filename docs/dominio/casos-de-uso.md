# Casos de uso — Sistema de Gestão de Planos de Voo

PUCRS · Projeto e Arquitetura de Software · 2026/2 · turma 340
Entrega de **05/10**.

**Ator único: Usuário** — quem monta, valida, aprova e gerencia os planos de
voo. O enunciado não distingue perfis (não há piloto-usuário, operador ou
administrador separados), então um ator só é o modelo fiel ao problema.

Cada caso de uso traz **pré-condição** e **resultado**, como pede a entrega, e
as **classes envolvidas**, para amarrar com o modelo de domínio.

---

## Malha aérea

### UC01 — Consultar aeroportos
- **Pré-condição:** nenhuma.
- **Fluxo:** o sistema lista os aeroportos cadastrados.
- **Resultado:** lista com código, nome e quantidade de vagas de cada aeroporto.
- **Classes:** `Aeroporto`.

### UC02 — Cadastrar aeroporto
- **Pré-condição:** o código informado ainda não existe.
- **Fluxo:** o usuário informa código, nome e quantidade de vagas.
- **Resultado:** aeroporto disponível para uso em aerovias e planos, **sem
  recompilar nem alterar código** — a malha vem do banco.
- **Classes:** `Aeroporto`.

### UC03 — Consultar aerovias
- **Pré-condição:** nenhuma.
- **Fluxo:** o sistema lista as aerovias cadastradas.
- **Resultado:** lista com identificador, origem, destino e distância em km.
  Cada aerovia vale **num sentido só**: POA→FLN (R1) e FLN→POA (R8) são
  registros distintos.
- **Classes:** `Aerovia`, `Aeroporto`.

### UC04 — Cadastrar aerovia
- **Pré-condição:** origem e destino já cadastrados; identificador inédito.
- **Fluxo:** o usuário informa identificador, aeroporto de origem, aeroporto de
  destino e distância.
- **Resultado:** aerovia disponível para uso em trechos. **Não há alteração nem
  exclusão de aerovia** — o que preserva os dados já referenciados por planos e
  reservas. Correção se faz criando uma aerovia nova, com identificador novo.
- **Classes:** `Aerovia`, `Aeroporto`.

---

## Pilotos e aeronaves

### UC05 — Consultar pilotos
- **Pré-condição:** nenhuma.
- **Resultado:** lista de pilotos disponíveis para uso em um plano.
- **Classes:** `Piloto`.

### UC06 — Cadastrar piloto
- **Pré-condição:** nenhuma.
- **Fluxo:** o usuário informa o nome do piloto.
- **Resultado:** piloto disponível para uso em planos.
- **Classes:** `Piloto`.

### UC07 — Consultar aeronaves
- **Pré-condição:** nenhuma.
- **Resultado:** lista com prefixo, categoria, velocidade de cruzeiro, autonomia
  e os dados próprios de cada categoria.
- **Classes:** `Aeronave` e subclasses.

### UC08 — Cadastrar aeronave
- **Pré-condição:** prefixo inédito; categoria conhecida.
- **Fluxo:** o usuário informa prefixo, categoria, velocidade de cruzeiro,
  autonomia e os dados próprios da categoria — empresa de manutenção
  (particular), companhia aérea e capacidade de passageiros (passageiros),
  companhia aérea e capacidade em toneladas (carga).
- **Resultado:** aeronave disponível para uso em planos, já com a regra de
  operação da sua categoria.
- **Classes:** `Aeronave` e subclasses.

---

## Planos de voo

### UC09 — Criar plano de voo
- **Pré-condição:** piloto, aeronave, aeroporto de origem e aeroporto de destino
  já cadastrados.
- **Fluxo:** o usuário informa piloto, aeronave, origem, destino e data e hora
  da partida.
- **Resultado:** plano criado com identificador e situação **EM_EDICAO**, ainda
  sem percurso e **sem nenhuma reserva**.
- **Classes:** `PlanoDeVoo`, `Piloto`, `Aeronave`, `Aeroporto`, `SituacaoPlano`.

### UC10 — Adicionar trecho ao percurso
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o usuário informa a aerovia, a altitude e o horário de entrada. Se
  a aeronave for pousar no aeroporto de destino daquele trecho, informa também a
  vaga pretendida e o tempo de permanência; caso contrário, é sobrevoo. O
  **horário de chegada é calculado pelo sistema** (distância ÷ velocidade de
  cruzeiro), nunca informado.
- **Resultado:** trecho acrescentado ao fim do percurso. Indicar vaga **não
  reserva** nada.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `Aerovia`.

### UC11 — Remover ou alterar trecho
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o usuário remove um trecho pela ordem, ou altera aerovia, altitude,
  horário de entrada ou dados do pouso.
- **Resultado:** percurso atualizado e reordenado. Nenhuma reserva é afetada,
  porque um plano em edição não tem reservas.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`.

### UC12 — Consultar altitudes disponíveis em uma aerovia
- **Pré-condição:** a aerovia existe.
- **Fluxo:** o usuário informa a aerovia e um período.
- **Resultado:** quais das 11 altitudes (25.000 a 35.000 pés, de 1.000 em 1.000)
  **não** têm reserva em nenhum dos slots de 1 hora do período. É consulta: não
  reserva nada e não altera estado.
- **Classes:** `Aerovia`, `ReservaAerovia`, `Slot`.

### UC13 — Consultar vagas disponíveis em um aeroporto
- **Pré-condição:** o aeroporto existe.
- **Fluxo:** o usuário informa o aeroporto e um período.
- **Resultado:** quais vagas estão livres em todos os slots do período. Também
  é só consulta.
- **Classes:** `Aeroporto`, `ReservaVaga`, `Slot`.

### UC14 — Validar plano
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o sistema confere, nesta ordem:
  1. **continuidade do percurso** — origem da 1ª aerovia = origem do plano,
     destino de cada aerovia = origem da seguinte, destino da última = destino
     do plano, e **pouso obrigatório no destino final**;
  2. **horários** — o horário de entrada de cada trecho bate com a chegada
     calculada do anterior (mais a permanência, se houve pouso);
  3. **mesmo dia** — a chegada ao destino final ocorre na data da partida;
  4. **aeronave** — a categoria pode operar naquela altitude e naquele horário;
  5. **autonomia** — a distância desde o último abastecimento não passa de 85%
     da autonomia nominal; sobrevoo acumula, pouso zera;
  6. **disponibilidade** — altitudes e vagas livres nos slots necessários.
- **Resultado:** **lista dos problemas encontrados** (vazia se estiver tudo
  certo). O plano **continua EM_EDICAO** e **nenhuma reserva é criada**.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `Slot`, `Aeronave`, `Aerovia`,
  `ReservaAerovia`, `ReservaVaga`.

### UC15 — Aprovar plano
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o sistema revalida (UC14) — porque a disponibilidade consultada
  antes pode ter mudado — e então cria as reservas: uma `ReservaAerovia` por
  slot de cada aerovia percorrida, na altitude do trecho, e uma `ReservaVaga`
  por slot de cada permanência em vaga.
- **Resultado:** **tudo ou nada, numa transação só**. Se qualquer reserva
  falhar, nenhuma permanece e o plano continua EM_EDICAO com a lista de
  problemas. Se todas forem criadas, o plano passa a **APROVADO**.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `Slot`, `ReservaAerovia`,
  `ReservaVaga`.

### UC16 — Consultar plano por identificador
- **Pré-condição:** nenhuma.
- **Resultado:** todos os dados do plano — percurso, horários calculados,
  reservas e situação — **em qualquer situação**, inclusive cancelado e
  abandonado.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `ReservaAerovia`, `ReservaVaga`.

### UC17 — Consultar planos por situação
- **Pré-condição:** nenhuma.
- **Fluxo:** o usuário filtra por EM_EDICAO, APROVADO, CANCELADO ou ABANDONADO;
  sem filtro, lista todos.
- **Resultado:** lista de planos da situação pedida.
- **Classes:** `PlanoDeVoo`, `SituacaoPlano`.

### UC18 — Abandonar plano
- **Pré-condição:** o plano está **EM_EDICAO**.
- **Fluxo:** o usuário abandona o plano.
- **Resultado:** plano passa a **ABANDONADO**. Não pode mais ser alterado,
  validado nem aprovado, mas continua consultável. Não há reserva a liberar,
  porque plano em edição nunca reservou nada.
- **Classes:** `PlanoDeVoo`, `SituacaoPlano`.

### UC19 — Cancelar plano aprovado
- **Pré-condição:** o plano está **APROVADO** e a **partida ainda não ocorreu**.
- **Fluxo:** o usuário cancela o plano.
- **Resultado:** plano passa a **CANCELADO** e **todas as suas reservas são
  liberadas**, devolvendo altitudes e vagas à disponibilidade. Plano aprovado
  cuja partida já ocorreu é voo executado: consultável, **não cancelável**.
- **Classes:** `PlanoDeVoo`, `ReservaAerovia`, `ReservaVaga`, `SituacaoPlano`.

---

## Mapa de estados

```
                 UC15 aprovar (validado + tudo reservado)
    EM_EDICAO ──────────────────────────────────────────► APROVADO
        │                                                     │
        │ UC18 abandonar          UC19 cancelar (só se a partida ainda não
        ▼                          ocorreu; libera todas as reservas)
    ABANDONADO                                                ▼
                                                          CANCELADO
```

Só **EM_EDICAO** é modificável (UC10, UC11, UC14, UC15, UC18). Planos
aprovados, cancelados e abandonados preservam o que tinham e continuam
consultáveis (UC16, UC17).
