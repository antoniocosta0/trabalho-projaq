# ADR 0013 — Gateway com prefixo por serviço e Eureka

**Decisão.** O Spring Cloud Gateway é o único ponto de entrada publicado (porta 8080). Cada
serviço ganha um prefixo (`/gestao`, depois `/cadastro` e `/registro`), removido antes do
encaminhamento (`StripPrefix=1`), e o destino é resolvido pelo Eureka (`lb://planos-de-voo`).
O gateway envia `X-Forwarded-*` (por isso `trusted-proxies`), e o backend os respeita
(`server.forward-headers-strategy=framework`), o que mantém Swagger e `Location` com o prefixo.

**Alternativas.**
- Rotas pelos caminhos originais, sem prefixo: `/aeroportos` e `/aerovias` existem tanto na
  gestão (disponibilidade) quanto no futuro cadastro, então as rotas colidiriam.
- Mudar as URLs da gestão para evitar a colisão: quebraria a API da parte 1.
- Endereço fixo do backend no gateway: sem descoberta nem balanceamento, o que o enunciado exige.

**Justificativa.** Os serviços mantêm suas URLs e o cliente conhece só o gateway. Quando a
gestão está parada, o gateway responde 503. Logo após a parada há uma janela de alguns segundos
com 500 (conexão recusada) até a lista de instâncias ser renovada; os intervalos de atualização do
Eureka e do gateway foram reduzidos para encurtá-la.
