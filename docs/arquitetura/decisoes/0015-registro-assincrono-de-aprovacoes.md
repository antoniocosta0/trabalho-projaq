# ADR 0015 — Registro assíncrono das aprovações por RabbitMQ

**Decisão.** A tarefa `AprovacaoRabbitPublisher` lê o outbox (ADR 0011) a cada 5 s, publica cada
aprovação pendente como mensagem persistente e só marca `publicado_em` depois do publisher
confirm do broker; falhas incrementam `tentativas` e guardam `ultimo_erro`. O serviço `registro`
consome a fila com ack automático do Spring (o ack sai quando o método termina sem exceção, depois
da gravação), ignora `eventoId` ou plano já registrado e, após 3 tentativas, rejeita a mensagem
para a DLQ. Os dois lados declaram a mesma topologia durável.

**Alternativas.**
- Ack manual no consumidor: mais código para o mesmo efeito do ack automático após o retorno.
- Reenfileirar sempre a mensagem que falhou: uma mensagem inválida ficaria em laço infinito.
- Topologia declarada só pelo consumidor: com o registro nunca iniciado, o broker aceitaria e
  descartaria as mensagens, porque não haveria fila ligada à exchange.
- Arquivo de definições carregado pelo RabbitMQ: exigiria também usuários e permissões no arquivo.

**Justificativa.** Atende à etapa 4: aprovar não depende do registro nem do broker, nenhuma
aprovação confirmada é esquecida (a intenção está no banco da gestão), reenvios não duplicam e
falhas têm diagnóstico (logs com tentativa e causa, colunas do outbox, DLQ). Não há transação
distribuída: o registro pode ficar atrasado e alcança a gestão quando volta (consistência eventual).
Recuperação da DLQ: no painel do RabbitMQ (`http://localhost:15672`, guest/guest), mover as
mensagens de `registro.aprovacoes.dlq` de volta para `registro.aprovacoes` depois de corrigir a causa.
