# Casos de uso

Ator único: **Usuário**. O enunciado não distingue perfis.

| UC | Caso de uso | Serviço (aplicação) | Endpoint pelo gateway |
|---|---|---|---|
| UC01 | Consultar aeroportos | `MalhaAereaService` (cadastro) | `GET /cadastro/aeroportos` |
| UC02 | Consultar aerovias | `MalhaAereaService` (cadastro) | `GET /cadastro/aerovias` |
| UC03 | Consultar pilotos | `FrotaService` (gestão) | `GET /gestao/pilotos` |
| UC04 | Consultar aeronaves | `FrotaService` (gestão) | `GET /gestao/aeronaves` |
| UC05 | Criar plano | `PlanoDeVooService` (gestão) | `POST /gestao/planos` |
| UC06 | Editar plano e percurso | `PlanoDeVooService` (gestão) | `PUT /gestao/planos/{id}`, `POST/PUT/DELETE /gestao/planos/{id}/trechos` |
| UC07 | Consultar altitudes disponíveis | `DisponibilidadeService` (gestão) | `GET /gestao/aerovias/{id}/altitudes-disponiveis` |
| UC08 | Consultar vagas disponíveis | `DisponibilidadeService` (gestão) | `GET /gestao/aeroportos/{codigo}/vagas-disponiveis` |
| UC09 | Validar plano | `PlanoDeVooService` (gestão) | `POST /gestao/planos/{id}/validacao` |
| UC10 | Aprovar plano | `PlanoDeVooService` (gestão) | `POST /gestao/planos/{id}/aprovacao` |
| UC11 | Abandonar plano | `PlanoDeVooService` (gestão) | `POST /gestao/planos/{id}/abandono` |
| UC12 | Cancelar plano | `PlanoDeVooService` (gestão) | `POST /gestao/planos/{id}/cancelamento` |
| UC13 | Consultar plano | `PlanoDeVooService` (gestão) | `GET /gestao/planos/{id}` |
| UC14 | Consultar planos por situação | `PlanoDeVooService` (gestão) | `GET /gestao/planos?situacao=` |

Os cadastros de aeroporto e aerovia (`POST /cadastro/...`) e de piloto e aeronave
(`POST /gestao/...`) existem porque o §2 e o §8.1 pedem, mas não têm regra de negócio além de
recusar duplicatas. Na parte 2, as rotas `/gestao/planos` exigem o cabeçalho `X-Usuario` e cada
aprovação efetivada é registrada no serviço `registro` (`GET /registro/aprovacoes/{planoId}`).

## Regras por caso de uso

- **UC05/UC06:** só plano `EM_EDICAO` pode ser alterado. A chegada de cada trecho é
  calculada (distância ÷ velocidade). Indicar vaga não reserva.
- **UC07/UC08:** só consultam. O recurso aparece livre se estiver livre em todos os slots
  do período.
- **UC09:** percurso contínuo, horários coerentes, pouso no destino, chegada no mesmo dia,
  regra da categoria, autonomia de 85% e disponibilidade. Não muda a situação nem reserva.
- **UC10:** refaz a validação e reserva tudo numa transação. Se algo falhar, nada fica
  reservado e o plano continua `EM_EDICAO`.
- **UC11:** só plano `EM_EDICAO`. Não há reserva a liberar.
- **UC12:** só plano `APROVADO` com partida futura. Libera as reservas.
