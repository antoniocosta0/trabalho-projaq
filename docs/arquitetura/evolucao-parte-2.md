# Evolução para microsserviços (parte 2)

Cada etapa termina numa tag (`etapa-1` a `etapa-4`). A parte 1 está na tag `parte-1`.

## Etapa 1 — Preparar o monólito para a extração

### Diagnóstico dos acoplamentos

Linhas referentes à tag `parte-1`. Caminhos a partir de `backend/src/main/java/com/pucrs/planosdevoo/`.

**Consultas a aeroportos, vagas e aerovias**

| Onde | O que faz |
|---|---|
| `domain/services/PlanoDeVooService.java:153,158` | busca aeroporto (criar e alterar plano) e aerovia (adicionar e alterar trecho) pelos repositórios locais |
| `domain/services/DisponibilidadeService.java:41,51` | confere a aerovia e lê a quantidade de vagas do aeroporto para a consulta de disponibilidade |
| `domain/model/PlanoDeVoo.java:167` | a validação pergunta ao `Aeroporto` se a vaga do pouso existe |
| `domain/services/MalhaAereaService.java` | cadastro e consulta da malha: é o que será extraído inteiro |

**Entidades, repositórios, tabelas e chaves estrangeiras**

| Onde | Dependência |
|---|---|
| `db/schema.sql:9,10` | `aerovia` → `aeroporto` (origem e destino) |
| `db/schema.sql:35,36` | `plano_de_voo` → `aeroporto` (origem e destino) |
| `db/schema.sql:44` | `trecho` → `aerovia` |
| `db/schema.sql:56` | `reserva_aerovia` → `aerovia` |
| `db/schema.sql:66` | `reserva_vaga` → `aeroporto` |
| `infra/.../entity/PlanoDeVooEntity.java:39,43`, `TrechoEmbeddable.java:16`, `ReservaAeroviaEntity.java:25`, `ReservaVagaEntity.java:25` | `@ManyToOne` para `AeroportoEntity` e `AeroviaEntity` |
| `infra/.../repository/PlanoDeVooRepositoryAdapter.java:40,41,54`, `ReservaRepositoryAdapter.java:53,58` | `getReferenceById` nos repositórios JPA da malha para gravar plano, trecho e reserva |
| `infra/.../mapper/PlanoDeVooMapper.java`, `ReservaMapper.java` | reconstroem `Aeroporto` e `Aerovia` completos a partir das entidades da malha |

O domínio do plano guarda objetos `Aeroporto` e `Aerovia` inteiros. Ler um plano do banco exige as
tabelas da malha, então elas não podem sair do banco da gestão sem mudar o modelo.

**Onde a aprovação é confirmada e limites transacionais**

| Onde | O que acontece |
|---|---|
| `domain/services/PlanoDeVooService.java:112-118` | uma transação (`TransacaoPort`) busca o plano, confere conflitos, muda a situação para `APROVADO` e grava as reservas |
| `domain/services/PlanoDeVooService.java:123-128` | uma transação muda a situação para `CANCELADO` e libera as reservas |

Não existe ponto de saída para avisar outra aplicação de que uma aprovação foi efetivada.

**Contratos necessários**

- Consulta da rede aérea: buscar um aeroporto (código, nome, vagas) e uma aerovia (id, origem, destino, distância) por identificador.
- Comunicação da aprovação: identificação do plano aprovado e o instante da aprovação, gravados junto com a aprovação.
- Propriedade do plano: o plano não tem dono, e a mensagem de aprovação exige o criador.

### O que mudou e por quê

| Mudança | Por quê | Mudança futura concentrada aqui |
|---|---|---|
| `MalhaAereaPort` (`domain/ports/out`) com `MalhaAereaLocalAdapter` | planos e disponibilidade deixam de usar os repositórios da malha | etapa 3 troca só o adaptador por um cliente HTTP |
| plano guarda códigos de aeroporto; trecho guarda cópia da aerovia; sem FKs para a malha | ler um plano não depende mais das tabelas `aeroporto` e `aerovia` | etapa 3 apaga essas tabelas sem tocar nos planos |
| a existência da vaga passou do `PlanoDeVoo` para `DisponibilidadeService.verificarRecursos`, via porta | a vaga cadastrada pertence ao cadastro; a ocupação, à gestão | o mesmo código passa a consultar o cadastro por HTTP |
| `AprovacaoPublisherPort` e tabela `outbox_evento`, gravada na transação da aprovação | toda aprovação efetivada passa a ter a intenção de envio registrada | etapa 4 acrescenta só o publicador e o RabbitMQ |
| cabeçalho `X-Usuario` e coluna `criador` | propriedade dos planos e criador na mensagem | o gateway apenas repassa o cabeçalho |

Decisões: ADRs 0010, 0011 e 0012. O `MalhaAereaService` e suas rotas continuam no monólito até a
etapa 3; é o bloco que será extraído inteiro.

### Evidências

- `./mvnw test` no `backend/`: regras da parte 1, propriedade (`PlanoDeVooServiceTest`) e outbox
  só em aprovação efetivada (validar e aprovação recusada não gravam).
- `docs/api/requests.http` executado do início ao fim num banco limpo, com os mesmos resultados
  da parte 1 e os novos 401 e 403.
- O schema mudou: para usar um banco criado antes desta etapa, apague `backend/data/`
  (ou `docker compose down -v`).

## Etapa 2 — Gateway e Eureka ao redor do monólito

### O que mudou

| Mudança | Onde |
|---|---|
| servidor Eureka | `eureka/` (porta 8761) |
| gateway com a rota `/gestao/**` → `lb://planos-de-voo`, sem o prefixo | `gateway/` (porta 8080) |
| gestão registrada no Eureka; aceita `X-Forwarded-*` do gateway | `backend/pom.xml`, `application.properties` |
| `Location` das respostas 201 montado a partir da URL da requisição | `infra/adapters/in/rest/Localizacao.java` |
| três serviços no `compose.yaml`; só gateway e Eureka publicam porta | `compose.yaml` |

Decisão: ADR 0013.

### Portas publicadas

| Serviço | Porta no host | Para quê |
|---|---|---|
| gateway | `127.0.0.1:8080` | toda requisição de negócio |
| eureka | `127.0.0.1:8761` | painel de instâncias (demonstração) |
| planos | nenhuma | só é alcançado pela rede interna do Compose |

### Como demonstrar

- `http://localhost:8761`: a aplicação `PLANOS-DE-VOO` aparece registrada.
- `docs/api/requests.http`, agora com `@host = http://localhost:8080/gestao`: todas as respostas
  (inclusive erros, 401 e 403) iguais às do acesso direto.
- Swagger pelo gateway: `http://localhost:8080/gestao/swagger-ui.html`.
- Destino indisponível: `docker compose stop planos` → o gateway responde 503 (500 nos primeiros
  segundos, até a lista de instâncias ser renovada). `docker compose start planos` → volta a
  responder assim que se registra de novo.

## Etapa 3 — Extração do cadastro de aeroportos e aerovias

### O que mudou

| Mudança | Onde |
|---|---|
| serviço novo com aeroportos, vagas (`vaga(aeroporto_codigo, numero)`) e aerovias, banco próprio e carga inicial | `cadastro/` |
| `MalhaAereaHttpAdapter` substitui `MalhaAereaLocalAdapter` | `backend/.../infra/adapters/out/http` |
| `Aeroporto` da gestão passa a ter a lista de vagas recebida do cadastro | `backend/.../domain/model/Aeroporto.java` |
| sai da gestão: `MalhaAereaService`, rotas `/aeroportos` e `/aerovias`, tabelas `aeroporto` e `aerovia` | `backend/` |
| rota `/cadastro/**` no gateway e serviço `cadastro` no Compose | `gateway/`, `compose.yaml` |

Decisão: ADR 0014. Contrato: `docs/api/cadastro.md`.

### Separação dos dados

- A existência e a numeração das vagas pertencem ao cadastro; as reservas por plano e slot
  continuam na gestão.
- A gestão guarda só códigos (plano, reservas) e a cópia da aerovia em cada trecho (ADR 0010).
  Nenhuma tabela, entidade ou chave estrangeira é compartilhada entre os bancos.
- Os planos e reservas da carga inicial foram preservados sem alteração: já usavam os códigos
  POA, FLN, CWB, GRU e R1 a R8, que o cadastro carrega com os mesmos dados.
- A carga do cadastro usa `MERGE ... WHEN NOT MATCHED`: reiniciar não duplica nem sobrescreve o
  que foi cadastrado pela API.

### Como demonstrar

- `docs/api/requests.http`: seções 1 e 8 usam `/cadastro`; o resto, `/gestao`. Mesmos resultados
  das etapas anteriores.
- Cliente independente: `curl http://localhost:8080/cadastro/aerovias?origem=POA`.
- Cadastro fora: `docker compose stop cadastro`. Consultar um plano continua funcionando; validar
  e aprovar respondem 503 e o plano continua em edição. `docker compose start cadastro` → volta.
- Novos recursos: cadastrar SDU e R9 (seção 8) e aprovar um plano por eles, sem alterar a gestão.

## Etapa 4 — Registro assíncrono das aprovações

### O que mudou

| Mudança | Onde |
|---|---|
| publicador do outbox com confirmação do broker; colunas `tentativas` e `ultimo_erro` | `backend/.../infra/adapters/out/messaging`, `outbox_evento` |
| contrato da mensagem (versão 1) | `AprovacaoMensagem`, `docs/api/mensagens.md` |
| serviço novo: consumidor idempotente, banco próprio e consulta REST | `registro/` |
| RabbitMQ, `registro` e rota `/registro/**` | `compose.yaml`, `gateway/` |

Decisão: ADR 0015 (e ADR 0011 para o outbox).

### Consistência eventual

A aprovação continua atômica no banco da gestão: situação, reservas e linha do outbox na mesma
transação. A publicação acontece depois, fora dela. Se o broker, a gestão ou o registro estiverem
fora, o registro fica atrasado e se completa quando eles voltam; nada é perdido nem duplicado.

### Como demonstrar (§7 da parte 2)

Com `APP_RELOGIO_INSTANTE_FIXO=2026-10-19T10:00:00 docker compose up -d --build --wait`:

| # | Demonstração | Como |
|---|---|---|
| 5 | aprovação chega ao registro | aprovar um plano; `GET /registro/aprovacoes/{id}` |
| 6 | validar e aprovação recusada não registram | `requests.http` 2.5 e 4.2; `GET /registro/aprovacoes/2` e `/7` dão 404 |
| 7 | consumidor parado | `docker compose stop registro`; aprovar; a mensagem espera na fila (painel em `:15672`); `docker compose start registro` → registrado |
| 8 | broker ou produtor fora | `docker compose stop rabbitmq`; aprovar (200); o log mostra "não publicada (tentativa N)"; `docker compose stop planos`; `start rabbitmq`; `start planos` → publicada e registrada |
| 9 | reenvio sem duplicar | publicar de novo, pelo painel, o JSON de `GET /registro/aprovacoes/1001` na exchange `planos.aprovacoes` com a chave `plano.aprovado`; o log diz "já registrada, ignorada" e a contagem não muda |
| 10 | cancelamento preserva o histórico | `requests.http` 5.1; `GET /registro/aprovacoes/1` continua igual |
| 11 | dados após reinício | `docker compose restart`; planos, cadastro, registros e outbox iguais; nada é reenviado |

Os planos aprovados da carga inicial (1001 a 1005) chegam ao registro pela fila na primeira
subida, com `eventoId` fixo; reinícios não os duplicam.
