# Roteiro da apresentação

Cada demonstração aponta para o bloco de [`api/requests.http`](api/requests.http).
Banco limpo e blocos na ordem. Para ensaiar em qualquer data com o resultado de 19/10:

```sh
cd backend && rm -rf ./data && ./mvnw spring-boot:run \
  -Dspring-boot.run.arguments=--app.relogio.instante-fixo=2026-10-19T10:00:00
```

| # | Demonstração | Bloco | Resultado |
|---|---|---|---|
| 1 | Consultar malha e frota | 1.1–1.4 | 4 aeroportos, 8 aerovias, 4 pilotos, 4 aeronaves |
| 2 | Criar plano e percurso | 2.3, 2.4 | chegada 23:26 calculada |
| 3 | Consultar disponibilidade | 2.1, 2.2 | 11 altitudes, 16 vagas |
| 4 | Validar sem reservar | 2.5–2.7 | `valido: true`, disponibilidade igual, `EM_EDICAO` |
| 5 | Aprovar e reservar | 2.8–2.10 | `APROVADO`, 10 altitudes, 15 vagas |
| 6 | Percurso inconsistente | 3.1 | "Trecho 2 sai de CWB, mas o trecho 1 termina em FLN" |
| 7 | Horário incompatível | 3.4 | "deveria ser às 19/10 08:26" |
| 8 | Restrição da aeronave | 3.2, 3.3 | particular a 30.000 pés; carga às 09:00 |
| 9 | Autonomia insuficiente | 3.5 | 865 km acima de 850 km |
| 10 | Conflito de altitude | 4.1, 4.4 | 4.4 conflita com o plano 1003 da carga inicial |
| 11 | Vaga indisponível | 4.3 | vaga 3 de FLN ocupada em dois slots |
| 12 | Aprovação sem reserva parcial | 4.2 | 422, plano `EM_EDICAO`, vaga 9 livre |
| 13 | Cancelar plano futuro | 5.1 | `CANCELADO`, recursos livres de novo |
| 14 | Cancelar plano que partiu | 5.2 | 409 para o plano 1005 |
| 15 | Alterar dados e abandonar | 6.1, 6.2 | chegada recalculada; `ABANDONADO` |
| 16 | Consultar por situação | 6.4–6.8 | todos continuam consultáveis |
| 17 | Modificar fora de edição | 6.3 | 409 |

Fora do roteiro: a seção 8 cadastra aeroporto, aerovia, piloto e aeronave e aprova um plano
com eles sem reiniciar a aplicação (§2). O Swagger UI fica em
`http://localhost:8080/swagger-ui.html`.
