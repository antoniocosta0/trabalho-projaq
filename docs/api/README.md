# API

| Arquivo | O que é |
|---|---|
| `requests.http` | roteiro de requisições, numerado e comentado |

O `requests.http` segue a ordem da apresentação de 19/10 e inclui os casos de
falha, não só o caminho feliz.

> **Cobertura em 18/10: 8 das 17 demonstrações** da seção 16.3 do `CLAUDE.md`.
> Faltam as de disponibilidade, aprovação, cancelamento e autonomia — cujo
> código já existe desde o PR #10 e só precisa ser ligado no roteiro — e as de
> conflito de altitude, vaga indisponível e cancelamento recusado, que dependem
> dos planos aprovados na carga inicial.

O formato é o mesmo do `testRequests.http` do exemplo da biblioteca, em
`PROJAQ/Exercícios e Trabalhos/SemanaDia31-08/ex1-biblioteca-v3-rotasComParametros/`.

A documentação formal da API é gerada por OpenAPI (springdoc), e o Swagger UI
serve como ferramenta de demonstração.
