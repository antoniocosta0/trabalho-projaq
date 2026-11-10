# Casos de uso

Ator único: **Usuário**. O enunciado não distingue perfis.

| UC | Caso de uso | Serviço | Endpoint |
|---|---|---|---|
| UC01 | Consultar aeroportos | `MalhaAereaService` | `GET /aeroportos` |
| UC02 | Consultar aerovias | `MalhaAereaService` | `GET /aerovias` |
| UC03 | Consultar pilotos | `FrotaService` | `GET /pilotos` |
| UC04 | Consultar aeronaves | `FrotaService` | `GET /aeronaves` |
| UC05 | Criar plano | `PlanoDeVooService` | `POST /planos` |
| UC06 | Editar plano e percurso | `PlanoDeVooService` | `PUT /planos/{id}`, `POST/PUT/DELETE /planos/{id}/trechos` |
| UC07 | Consultar altitudes disponíveis | `DisponibilidadeService` | `GET /aerovias/{id}/altitudes-disponiveis` |
| UC08 | Consultar vagas disponíveis | `DisponibilidadeService` | `GET /aeroportos/{codigo}/vagas-disponiveis` |
| UC09 | Validar plano | `PlanoDeVooService` | `POST /planos/{id}/validacao` |
| UC10 | Aprovar plano | `PlanoDeVooService` | `POST /planos/{id}/aprovacao` |
| UC11 | Abandonar plano | `PlanoDeVooService` | `POST /planos/{id}/abandono` |
| UC12 | Cancelar plano | `PlanoDeVooService` | `POST /planos/{id}/cancelamento` |
| UC13 | Consultar plano | `PlanoDeVooService` | `GET /planos/{id}` |
| UC14 | Consultar planos por situação | `PlanoDeVooService` | `GET /planos?situacao=` |

Os cadastros de aeroporto, aerovia, piloto e aeronave (`POST` nas mesmas rotas de consulta)
existem porque o §2 e o §8.1 pedem, mas não têm regra de negócio além de recusar duplicatas.

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
