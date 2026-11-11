# Contrato HTTP do cadastro da rede aérea

Serviço `cadastro`. Pelo gateway: `http://localhost:8080/cadastro`. OpenAPI em
`/cadastro/v3/api-docs` e Swagger em `/cadastro/swagger-ui.html`. JSON em UTF-8; erros no
formato Problem Detail (`status`, `title`, `detail` e, em 400, `problemas`).

| Método e rota | Entrada | Resposta | Erros |
|---|---|---|---|
| `GET /aeroportos` | — | 200 `[{codigo, nome, quantidadeVagas}]` | — |
| `GET /aeroportos/{codigo}` | — | 200 `{codigo, nome, quantidadeVagas}` | 404 |
| `GET /aeroportos/{codigo}/vagas` | — | 200 `{aeroporto, vagas: [1, 2, ...]}` | 404 |
| `POST /aeroportos` | `{codigo, nome, quantidadeVagas}` | 201 + `Location`; cria as vagas 1..N | 400, 409 (código repetido), 422 |
| `GET /aerovias?origem=&destino=` | filtros opcionais | 200 `[{id, origem, destino, distanciaKm}]` | — |
| `GET /aerovias/{id}` | — | 200 `{id, origem, destino, distanciaKm}` | 404 |
| `POST /aerovias` | `{id, origem, destino, distanciaKm}` | 201 + `Location` | 400, 404 (aeroporto inexistente), 409 (id repetido), 422 |

Não há alteração nem exclusão: aerovias são imutáveis, e um id repetido nunca substitui o
registro existente.

## Uso pela gestão de planos

A gestão chama só `GET /aeroportos/{codigo}/vagas` e `GET /aerovias/{id}`, pelo
`MalhaAereaHttpAdapter`, resolvendo `cadastro` pelo Eureka.

| Situação | O que a gestão responde |
|---|---|
| 404 do cadastro | 404 "Aeroporto/Aerovia ... não encontrado(a)" |
| cadastro sem instância, conexão recusada, 5xx ou tempo esgotado | 503 "O cadastro da rede aérea está indisponível no momento" |

Tempos limite: conexão 2 s e leitura 3 s (`cadastro.timeout.conexao` e `cadastro.timeout.leitura`).
Com o cadastro fora, validar e aprovar respondem 503 e o plano continua `EM_EDICAO`; consultar um
plano continua funcionando, porque o trecho guarda a cópia da aerovia.
