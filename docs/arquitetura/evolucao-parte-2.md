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
