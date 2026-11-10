# ADR 0008 — Interpretações do enunciado

Pontos em que o enunciado não é explícito e a escolha adotada.

1. **Slot semiaberto `[início, fim)`.** Um uso das 15:00 às 16:00 ocupa um slot. A outra
   leitura cobraria um slot não usado.
2. **Janela da carga.** Entrada e saída dentro de `[00:00, 06:00)` do mesmo dia: chegar às
   06:00 já está fora, como diz o §3.
3. **Duração arredondada ao minuto.** 363 km a 830 km/h dá 26 min 14 s; usamos 26 min.
   Sem isso nenhum horário digitado bateria com a chegada calculada.
4. **Aprovar plano com partida passada é permitido.** O enunciado só proíbe cancelar
   (§6.8). Não inventamos uma regra que ele não pede.
5. **Relógio injetado.** O "agora" vem de um `Clock`, fixável por
   `app.relogio.instante-fixo` para ensaiar a apresentação em outro dia.
6. **Vaga só é reservada em pouso.** A origem não reserva vaga (§4.2 e §4.3).
7. **Cabeçalho editável em edição.** `PUT /planos/{id}` troca piloto, aeronave, origem,
   destino e partida; os trechos são mantidos e as chegadas recalculadas (§6.1).
8. **Ids da carga inicial a partir de 1001**, para os planos criados pela API começarem em 1.
