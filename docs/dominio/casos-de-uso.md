# Casos de uso — Sistema de Gestão de Planos de Voo

PUCRS · Projeto e Arquitetura de Software · 2026/2 · turma 340
Entrega de **05/10**.

**Ator único: Usuário** — quem monta, valida, aprova e gerencia os planos de
voo. O enunciado não distingue perfis (não há piloto-usuário, operador ou
administrador separados), então um ator só é o modelo fiel ao problema.

**Cadastros fora da lista.** Por orientação do professor, cadastrar aeroporto,
aerovia, piloto e aeronave **não conta como caso de uso**: são operações de
manutenção de dados, sem regra de negócio. A malha, os pilotos e as aeronaves
já vêm na carga inicial (`data.sql`). Mesmo assim, como o enunciado (§2 e §8.1)
pede que seja possível cadastrar novos aeroportos e aerovias, a API mantém os
endpoints de cadastro — ver o fim deste arquivo.

Cada caso de uso traz **pré-condição** e **resultado**, como pede a entrega, e
as **classes envolvidas**, para amarrar com o modelo de domínio.

---

## Malha aérea

### UC01 — Consultar aeroportos
- **Pré-condição:** nenhuma.
- **Fluxo:** o sistema lista os aeroportos cadastrados.
- **Resultado:** lista com código, nome e quantidade de vagas de cada aeroporto.
- **Classes:** `Aeroporto`.

### UC02 — Consultar aerovias
- **Pré-condição:** nenhuma.
- **Fluxo:** o sistema lista as aerovias cadastradas.
- **Resultado:** lista com identificador, origem, destino e distância em km.
  Cada aerovia vale **num sentido só**: POA→FLN (R1) e FLN→POA (R8) são
  registros distintos.
- **Classes:** `Aerovia`, `Aeroporto`.

---

## Pilotos e aeronaves

### UC03 — Consultar pilotos
- **Pré-condição:** nenhuma.
- **Resultado:** lista de pilotos disponíveis para uso em um plano.
- **Classes:** `Piloto`.

### UC04 — Consultar aeronaves
- **Pré-condição:** nenhuma.
- **Resultado:** lista com prefixo, categoria, velocidade de cruzeiro, autonomia
  e os dados próprios de cada categoria.
- **Classes:** `Aeronave` e subclasses.

---

## Planos de voo

### UC05 — Criar plano de voo
- **Pré-condição:** piloto, aeronave, aeroporto de origem e aeroporto de destino
  já cadastrados.
- **Fluxo:** o usuário informa piloto, aeronave, origem, destino e data e hora
  da partida.
- **Resultado:** plano criado com identificador e situação **EM_EDICAO**, ainda
  sem percurso e **sem nenhuma reserva**.
- **Classes:** `PlanoDeVoo`, `Piloto`, `Aeronave`, `Aeroporto`, `SituacaoPlano`.

### UC06 — Definir ou alterar o percurso
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o usuário acrescenta, altera ou remove trechos. Em cada trecho
  informa a aerovia, a altitude e o horário de entrada. Se a aeronave for pousar
  no aeroporto de destino daquele trecho, informa também a vaga pretendida e o
  tempo de permanência; caso contrário, é sobrevoo. O **horário de chegada é
  calculado pelo sistema** (distância ÷ velocidade de cruzeiro), nunca
  informado.
- **Resultado:** percurso atualizado e reordenado. Indicar vaga **não reserva**
  nada, e nenhuma reserva é afetada, porque um plano em edição não tem reservas.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `Aerovia`.

### UC07 — Consultar altitudes disponíveis em uma aerovia
- **Pré-condição:** a aerovia existe.
- **Fluxo:** o usuário informa a aerovia e um período.
- **Resultado:** quais das 11 altitudes (25.000 a 35.000 pés, de 1.000 em 1.000)
  **não** têm reserva em nenhum dos slots de 1 hora do período. É consulta: não
  reserva nada e não altera estado.
- **Classes:** `Aerovia`, `ReservaAerovia`, `Slot`.

### UC08 — Consultar vagas disponíveis em um aeroporto
- **Pré-condição:** o aeroporto existe.
- **Fluxo:** o usuário informa o aeroporto e um período.
- **Resultado:** quais vagas estão livres em todos os slots do período. Também
  é só consulta.
- **Classes:** `Aeroporto`, `ReservaVaga`, `Slot`.

### UC09 — Validar plano
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

### UC10 — Aprovar plano
- **Pré-condição:** o plano existe e está **EM_EDICAO**.
- **Fluxo:** o sistema revalida (UC09) — porque a disponibilidade consultada
  antes pode ter mudado — e então cria as reservas: uma `ReservaAerovia` por
  slot de cada aerovia percorrida, na altitude do trecho, e uma `ReservaVaga`
  por slot de cada permanência em vaga.
- **Resultado:** **tudo ou nada, numa transação só**. Se qualquer reserva
  falhar, nenhuma permanece e o plano continua EM_EDICAO com a lista de
  problemas. Se todas forem criadas, o plano passa a **APROVADO**.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `Slot`, `ReservaAerovia`,
  `ReservaVaga`.

### UC11 — Abandonar plano
- **Pré-condição:** o plano está **EM_EDICAO**.
- **Fluxo:** o usuário abandona o plano.
- **Resultado:** plano passa a **ABANDONADO**. Não pode mais ser alterado,
  validado nem aprovado, mas continua consultável. Não há reserva a liberar,
  porque plano em edição nunca reservou nada.
- **Classes:** `PlanoDeVoo`, `SituacaoPlano`.

### UC12 — Cancelar plano aprovado
- **Pré-condição:** o plano está **APROVADO** e a **partida ainda não ocorreu**.
- **Fluxo:** o usuário cancela o plano.
- **Resultado:** plano passa a **CANCELADO** e **todas as suas reservas são
  liberadas**, devolvendo altitudes e vagas à disponibilidade. Plano aprovado
  cuja partida já ocorreu é voo executado: consultável, **não cancelável**.
- **Classes:** `PlanoDeVoo`, `ReservaAerovia`, `ReservaVaga`, `SituacaoPlano`.

### UC13 — Consultar plano por identificador
- **Pré-condição:** nenhuma.
- **Resultado:** todos os dados do plano — percurso, horários calculados,
  reservas e situação — **em qualquer situação**, inclusive cancelado e
  abandonado.
- **Classes:** `PlanoDeVoo`, `Trecho`, `Pouso`, `ReservaAerovia`, `ReservaVaga`.

### UC14 — Consultar planos por situação
- **Pré-condição:** nenhuma.
- **Fluxo:** o usuário filtra por EM_EDICAO, APROVADO, CANCELADO ou ABANDONADO;
  sem filtro, lista todos.
- **Resultado:** lista de planos da situação pedida.
- **Classes:** `PlanoDeVoo`, `SituacaoPlano`.

---

## Mapa de estados

```
                 UC10 aprovar (validado + tudo reservado)
    EM_EDICAO ──────────────────────────────────────────► APROVADO
        │                                                     │
        │ UC11 abandonar          UC12 cancelar (só se a partida ainda não
        ▼                          ocorreu; libera todas as reservas)
    ABANDONADO                                                ▼
                                                          CANCELADO
```

Só **EM_EDICAO** é modificável (UC06, UC09, UC10, UC11). Planos
aprovados, cancelados e abandonados preservam o que tinham e continuam
consultáveis (UC13, UC14).

---

## Cadastros (fora da lista de casos de uso)

Não são casos de uso, mas existem na API para atender o enunciado (§2 e §8.1) e
para que um aeroporto ou aerovia nova possa ser incluída **sem alterar código**:

- **Cadastrar aeroporto:** código inédito, nome e quantidade de vagas.
- **Cadastrar aerovia:** identificador inédito, origem e destino já cadastrados
  e distância. **Sem alteração e sem exclusão** depois de criada.
- **Cadastrar piloto:** nome.
- **Cadastrar aeronave:** prefixo inédito, categoria, velocidade de cruzeiro,
  autonomia e os dados próprios da categoria.
