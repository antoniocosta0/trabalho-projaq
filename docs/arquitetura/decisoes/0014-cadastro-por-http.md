# ADR 0014 — Cadastro da rede aérea como serviço consultado por HTTP

**Decisão.** Aeroportos, vagas e aerovias moram no serviço `cadastro`, com banco próprio
(`cadastro/data`). A gestão não tem mais essas tabelas: usa `MalhaAereaPort`, agora implementada
por `MalhaAereaHttpAdapter` com `RestClient`, balanceamento pelo Eureka e tempos limite. 404 vira
recurso inexistente; qualquer outra falha vira 503. Não há circuit breaker.

**Alternativas.**
- OpenFeign: menos código por chamada, mas mais uma dependência para duas consultas.
- Circuit breaker (Resilience4j): evita esperar o tempo limite a cada chamada com o cadastro fora,
  mas o tempo limite curto já resolve para o volume do trabalho.
- Responder com dados em cache quando o cadastro cai: o enunciado proíbe aprovar com dados que
  não foram obtidos.

**Justificativa.** HTTP, descoberta, DTOs de transporte e tratamento de falha ficam só no
adaptador; nenhuma regra de negócio mudou. Os planos e reservas existentes foram preservados
porque já referenciam a malha por código (ADR 0010), e a carga do cadastro usa os mesmos códigos.
O balanceamento foi aplicado só ao cliente do cadastro: um `RestClient.Builder` global com
`@LoadBalanced` passa a ser usado pelo próprio cliente do Eureka, que deixa de se registrar.
