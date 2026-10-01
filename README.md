# Sistema de Gestão de Planos de Voo

Trabalho Final (parte 1) de **Projeto e Arquitetura de Software** — PUCRS,
turma 340, 2026/2. Backend em Java + Spring Boot, com API REST e persistência
em banco de dados.

> **Esqueleto.** As seções marcadas com `[PREENCHER]` são exigidas pelo
> enunciado (seção 8.5) e precisam estar completas antes da entrega. O critério
> é: alguém que nunca viu o projeto clona e roda sem perguntar nada.

## Equipe

`[PREENCHER: integrantes]`

## Data de referência

`[PREENCHER: AAAA-MM-DD]`

É a data usada nos testes de disponibilidade, conflito e reserva durante a
apresentação. Na carga inicial, **pelo menos 5 das 8 aerovias** têm ocupação
nessa data, toda ela consequência de planos aprovados existentes na base.

## Preparação do ambiente

`[PREENCHER: versões de Java e Maven; banco e como subi-lo]`

## Execução

`[PREENCHER]`

```sh
cd backend && ./mvnw spring-boot:run
```

## Banco e carga inicial

`[PREENCHER: como o schema é criado, como a carga inicial é aplicada, e como
recomeçar do zero]`

A carga inicial é reproduzível a partir dos artefatos deste repositório, sem
nenhuma inclusão manual.

## API

`[PREENCHER: URL base]`

- Documentação OpenAPI (Swagger UI): `[PREENCHER]`
- Roteiro de requisições: `docs/api/requests.http`

## Documentação

| O que | Onde |
|---|---|
| Guia de contexto do repositório | `CLAUDE.md` |
| Padrões de desenvolvimento | `docs/padroes-desenvolvimento.md` |
| Modelo de domínio (UML) | `docs/dominio/` |
| Diagramas C4 (contexto, containers, componentes) | `docs/arquitetura/` |
| Decisões arquiteturais (ADR) | `docs/arquitetura/decisoes/` |
| Registro do trabalho | `logs/` |
| Enunciado | `PAS_TF_2026_2_PlanosDeVoo (1).md` |
| Cronograma | `PAS_TF_2026_2_CronogramaPlanosDeVoo_P1.pdf` |
