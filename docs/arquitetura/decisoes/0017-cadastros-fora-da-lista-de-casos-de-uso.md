# ADR 0017 — Cadastros fora da lista de casos de uso

**Data:** 2026-10-18 (parte 1), revisitada em 2026-11-11 na extração do cadastro
**Situação:** aceita

## Contexto

Na parte 1, o professor orientou que cadastrar aeroporto, aerovia, piloto e aeronave não são
casos de uso do trabalho — o domínio do problema é o plano de voo, não a manutenção de tabelas de
apoio — mesmo que a API precise manter esses endpoints (§8.1 da parte 1). A decisão ficou registrada
só em `docs/padroes-desenvolvimento.md` da parte 1, sem ADR própria.

Na parte 2, o cadastro de aeroportos e aerovias foi extraído para o microsserviço `cadastro`
(etapa 3). A pergunta se repete num novo lugar: os endpoints `POST /aeroportos` e `POST /aerovias`
desse serviço contam como caso de uso dele?

## Alternativas consideradas

1. **Tratar os cadastros do novo serviço como caso de uso, já que agora têm uma aplicação própria.**
   Descartada: a extração mudou onde o código mora, não a natureza da operação. Cadastrar um
   aeroporto continua sendo gravar nome e vagas, sem regra de negócio — o mesmo raciocínio da
   parte 1 vale aqui.
2. **Manter os cadastros fora da lista de casos de uso também no `cadastro`**, documentando-os em
   seção própria. Escolhida, por continuidade com a decisão original.

## Decisão

Os cadastros de aeroporto e aerovia (agora no serviço `cadastro`) e de piloto e aeronave (que
continuam no `backend`, fora do escopo da extração) permanecem fora da lista de casos de uso
(`docs/dominio/casos-de-uso.md`, UC01–UC14) e continuam expostos como `POST` nos recursos
correspondentes, para atender o §2.3 da parte 2 (a API do cadastro precisa permitir incluir novos
recursos) e o teste de extensibilidade da parte 1 (§2).

## Consequências

A lista de casos de uso continua só com o que tem comportamento de domínio. Os endpoints de
cadastro seguem a mesma arquitetura hexagonal do resto de cada serviço — porta de entrada, DTO,
controller sem regra — e não são tratados como exceção por serem CRUD. A aplicação a um serviço
novo (`cadastro`) confirma que a decisão original não era específica do `backend` monolítico.
