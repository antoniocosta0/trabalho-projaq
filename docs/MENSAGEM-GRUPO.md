Gente, fiz uma auditoria no código e um levantamento do que falta pra entrega da parte 2 (prazo dia 29/11, domingo, 23:55). Resumo rápido e já com uma proposta de divisão, ajustem como quiserem:

A boa notícia: as 4 etapas já estão implementadas (valeu Vicenzo!), build e testes passando, 17 ADRs escritos, README e diagramas atualizados. Não tem bug nem regra de negócio errada. O que falta é mais nivelar o conhecimento do grupo e fechar dois testes.

*Pedro*
- Estudar a etapa 2 (Gateway + Eureka): pasta `gateway/`, `eureka/` e o ADR 0013
- Testar os cenários de "serviço fora do ar" do roteiro de demonstração
- Se preparar pra conduzir essa parte na apresentação
- Prazo: até 23/11

*João*
- Estudar a etapa 3 (cadastro): pasta `cadastro/`, ADR 0014 e `docs/api/cadastro.md`
- Escrever o teste de integração que falta no cadastro (teste de controller, não só de serviço)
- Se preparar pra conduzir essa parte
- Prazo: até 25/11

*Gustavo*
- Estudar a etapa 4 (RabbitMQ + registro): pasta `registro/`, ADR 0015 e `docs/api/mensagens.md`
- Escrever o teste de integração que falta no registro
- Se preparar pra conduzir essa parte
- Prazo: até 25/11

*Vicenzo*
- Validar a stack completa com docker compose rodando (as 11 demonstrações do enunciado, de ponta a ponta)
- Dar apoio pro Pedro, João e Gustavo nas etapas que eles vão estudar
- Prazo da validação: até 23/11

*Antônio*
- Coordenar as branches e revisar os PRs
- Levar pro grupo a decisão de apresentar por etapa nas aulas de acompanhamento ou tudo junto dia 30/11
- Organizar o ensaio geral
- Manter o README e o plano atualizados

Cronograma:
- 19 a 21/11: estudo individual de cada etapa
- 22 a 23/11: testes faltantes e correções
- 24/11: decidimos juntos como vai ser a apresentação
- 25 e 26/11: ensaio geral com a stack rodando, cada um conduzindo a etapa que estudou
- 27/11: revisão cruzada, cada um explica a etapa do outro (é o teste real de "todo mundo defende o trabalho inteiro")
- 28/11: buffer pra ajustar o que sobrar
- 29/11 até 23:55: entrega

Prazo geral: domingo 29/11, 23:55. Lembrando que sem apresentação o trabalho conta como não entregue, e **todo mundo precisa conseguir defender o trabalho completo**, não só a parte que fez. Qualquer coisa chama aqui.
