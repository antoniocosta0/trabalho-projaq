# Roteiro da apresentação

As 17 demonstrações exigidas, na ordem, com o bloco correspondente em
[`api/requests.http`](api/requests.http). Esta tabela é o mapa; o arquivo
`.http` é o que se executa, e cada bloco lá traz a resposta real conferida
contra a aplicação.

**Antes de começar:** banco limpo e aplicação no ar. O caminho feliz parte às
**23:00** de 19/10, para a aprovação não cair se a banca for depois das 08:00.
A apresentação precisa terminar **antes das 23:00**. Para ensaiar em qualquer
data com o mesmo resultado do dia 19/10 às 10:00:

```sh
cd backend && rm -rf ./data && ./mvnw spring-boot:run \
  -Dspring-boot.run.arguments=--app.relogio.instante-fixo=2026-10-19T10:00:00
```

Sem o relógio fixo, no próprio dia 19/10 durante o horário de aula, o roteiro
também funciona — os voos aprovados ainda são futuros.

Os blocos supõem execução **na ordem**: os planos criados recebem id 1, 2, 3… em
sequência. Os planos 1001 a 1005 vêm da carga inicial.

| # | O que demonstrar | Bloco | Resultado esperado |
|---|---|---|---|
| 1 | Consultar a malha aérea | 1.1 a 1.4 | 4 aeroportos, 8 aerovias orientadas, 4 pilotos, 4 aeronaves com os dados da sua categoria |
| 2 | Criar plano e montar o percurso | 2.3, 2.4 | 201; `horarioChegada` 23:26 **calculado** pelo sistema |
| 3 | Consultar disponibilidade | 2.1, 2.2 | 11 altitudes e 16 vagas livres |
| 4 | Validar **sem** reservar | 2.5 a 2.7 | `valido: true`; a disponibilidade **não muda** e o plano segue `EM_EDICAO` |
| 5 | Aprovar **com** reserva | 2.8 a 2.10 | `APROVADO`; 10 altitudes e 15 vagas |
| 6 | Falha: percurso inconsistente | 3.1 | "Trecho 2 sai de CWB, mas o trecho 1 termina em FLN" |
| 7 | Falha: horário incompatível | 3.4 | "entrada às 19/10 11:00, mas deveria ser às 19/10 08:26" |
| 8 | Falha: restrição da aeronave | 3.2, 3.3 | particular a 30.000 pés; carga às 09:00, fora da janela 00:00–06:00 |
| 9 | Falha: autonomia insuficiente | 3.5 | "865 km sem abastecer, acima do limite de 850 km" |
| 10 | Falha: conflito de altitude | 4.1, **4.4** | 4.4 é o conflito contra um plano **da carga inicial** |
| 11 | Falha: vaga indisponível | 4.3 | a vaga 3 de FLN ocupada em **dois** slots pela permanência |
| 12 | Aprovação recusada **sem reserva parcial** | 4.2, 4.2.a, 4.2.b | 422 com a lista; plano segue `EM_EDICAO`; disponibilidade **idêntica** |
| 13 | Cancelar plano futuro, liberando reservas | 5.1, 5.1.a, 5.1.b | `CANCELADO`; voltam as 11 altitudes e as 16 vagas |
| 14 | Cancelar plano que já partiu → recusado | **5.2** | 409, "a partida (17/10 08:00) já ocorreu" |
| 15 | Abandonar plano em edição | 6.2 | `ABANDONADO`, sem reserva a liberar |
| 16 | Consultar por situação | 6.4 a 6.8 | aprovados, cancelados e abandonados seguem consultáveis |
| 17 | Modificar plano fora de edição → recusado | 6.3 | 409, "só planos EM_EDICAO podem ser modificados" |

## Dois blocos que dependem do dia

A demonstração **14** tem duas versões:

- **5.2** usa o plano **1005** da carga inicial, que parte em 17/10. Ele já
  partiu em qualquer dia, então o 409 sai sempre. **É a versão para ensaiar.**
- **5.3** usa os planos 1001 e 1004, que partem em 19/10 de madrugada e à noite.
  Essa é a versão realista para o dia da apresentação, mas ensaiando antes o
  cancelamento é **aceito**, porque os voos ainda não partiram.

Para ensaiar o 5.3 em qualquer data, suba com o relógio fixo:

```sh
cd backend
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments=--app.relogio.instante-fixo=2026-10-19T10:00:00
```

## Depois das 17

A seção **8** do `requests.http` fecha com o teste de extensibilidade do §2 do
enunciado: cadastrar um aeroporto e uma aerovia novos e usá-los num plano
aprovado, **sem reiniciar a aplicação**. É o que o professor costuma pedir fora
do roteiro.

A seção **7** cobre os erros: 404 de recurso e de rota inexistente, 400 com a
lista de campos, 405 de método, 415 sem `Content-Type`, 422 de período
invertido, e `PUT`/`DELETE` de aerovia devolvendo 404 porque as rotas não
existem — a imutabilidade é estrutural.

## Se pedirem algo fora do roteiro

- **Swagger UI** em `http://localhost:8080/swagger-ui.html`: as 20 operações
  estão descritas lá, com os parâmetros e os códigos de erro de cada uma.
- **Console do H2** em `http://localhost:8080/h2-console` (JDBC
  `jdbc:h2:file:./data/planosdevoo`, usuário `sa`, senha em branco), para
  mostrar as tabelas de reserva por trás das consultas de disponibilidade.
- **A R1 e a R3 estão livres o dia inteiro** na data de referência, de
  propósito: sempre há recurso disponível para montar um plano novo na hora.
