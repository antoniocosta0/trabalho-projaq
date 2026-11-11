# ADR 0011 — Outbox de aprovações

**Decisão.** Ao aprovar, `PlanoDeVooService` chama `AprovacaoPublisherPort.publicar(...)` dentro
da mesma transação da situação e das reservas. O adaptador grava uma linha em `outbox_evento`
(id do evento, id do plano, instante da aprovação, `publicado_em` vazio). Na etapa 4, uma tarefa
agendada lê as linhas pendentes, monta a mensagem a partir do plano e marca `publicado_em` só
depois da confirmação do RabbitMQ. Os planos aprovados da carga inicial entram no outbox com
`evento_id` fixo.

**Alternativas.**
- Publicar direto no RabbitMQ depois do commit: se a aplicação ou o broker cair entre o commit e
  o envio, a aprovação some do registro.
- Publicar dentro da transação: o broker pode receber uma aprovação que depois sofre rollback.
- Gravar o JSON da mensagem no outbox: congelaria o conteúdo, mas plano aprovado já não muda,
  e a carga inicial teria que trazer JSON escrito à mão.

**Justificativa.** O §4 da etapa 4 exige publicação recuperável sem transação distribuída. A
intenção de envio nasce e morre junto com a aprovação; o envio pode atrasar, mas não se perde.
