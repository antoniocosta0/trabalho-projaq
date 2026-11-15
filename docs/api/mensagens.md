# Contrato da mensagem de aprovação (versão 1)

Produtor: gestão de planos. Consumidor: registro. Só uma aprovação efetivada gera mensagem.

## Topologia RabbitMQ

| Elemento | Nome | Propriedades |
|---|---|---|
| exchange | `planos.aprovacoes` | direct, durável |
| fila | `registro.aprovacoes` | durável, ligada com a chave `plano.aprovado`; mensagens rejeitadas vão para a DLQ |
| fila de falhas | `registro.aprovacoes.dlq` | durável |

A gestão e o registro declaram a mesma topologia ao conectar: a fila existe mesmo que o registro
nunca tenha subido. Mensagens são persistentes, com `content_type=application/json` e
`message_id` igual a `eventoId`.

## Corpo

```json
{
  "eventoId": "c70ab8f3-…",
  "versao": 1,
  "aprovadoEm": "2026-10-19T10:00:00",
  "plano": {
    "id": 1,
    "criador": "ana",
    "piloto": { "id": 1, "nome": "Ana Souza" },
    "aeronave": {
      "prefixo": "PR-PAX", "categoria": "PASSAGEIROS",
      "velocidadeCruzeiroKmh": 830.0, "autonomiaKm": 5000.0,
      "companhiaAerea": "Linhas Aéreas do Sul", "capacidadePassageiros": 180
    },
    "origem": "POA",
    "destino": "FLN",
    "partida": "2026-10-19T23:00:00",
    "trechos": [
      {
        "ordem": 1, "aerovia": "R1", "origem": "POA", "destino": "FLN", "altitude": 30000,
        "entrada": "2026-10-19T23:00:00", "chegada": "2026-10-19T23:26:00",
        "noDestino": "POUSO", "numeroVaga": 3, "permanenciaMinutos": 60,
        "saidaDaVaga": "2026-10-20T00:26:00"
      }
    ]
  }
}
```

- Datas em ISO-8601, sem fuso (horário de São Paulo).
- `aeronave` traz só os campos da categoria: `empresaManutencao` (PARTICULAR),
  `companhiaAerea` e `capacidadePassageiros` (PASSAGEIROS), `companhiaAerea` e
  `capacidadeToneladas` (CARGA).
- Trecho com `noDestino: "SOBREVOO"` não tem `numeroVaga`, `permanenciaMinutos` nem `saidaDaVaga`.
- Não há credenciais nem senhas na mensagem.
- Os planos aprovados da carga inicial usam `eventoId` fixo `00000000-0000-0000-0000-00000000NNNN`.

## Garantias

| Lado | Comportamento |
|---|---|
| produtor | a intenção de envio é gravada no outbox na transação da aprovação; o envio só é marcado como feito após a confirmação (publisher confirm) do broker; falhas ficam pendentes e são tentadas de novo a cada 5 s |
| consumidor | o ack só sai depois da gravação; um `eventoId` ou plano já registrado é ignorado; falha de processamento tem 3 tentativas e depois a mensagem vai para a DLQ |

Uma confirmação do broker não significa que o registro já gravou: o registro pode estar parado,
e a mensagem fica na fila até ele voltar (consistência eventual).

## Consulta no registro

`http://localhost:8080/registro`:

| Rota | Resposta |
|---|---|
| `GET /aprovacoes` | 200 `[{eventoId, planoId, aprovadoEm, recebidoEm}]` |
| `GET /aprovacoes/{planoId}` | 200 com a mensagem exatamente como foi recebida; 404 se não há registro |
