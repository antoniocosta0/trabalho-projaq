# API

| Arquivo | O que é |
|---|---|
| `requests.http` | roteiro de requisições, numerado e comentado |

O `requests.http` segue a ordem da apresentação de 19/10 e inclui os casos de
falha, não só o caminho feliz.

> **Cobertura: as 17 demonstrações** da seção 16.3 do `CLAUDE.md`, fechadas em
> 18/10. Todas foram **executadas contra a aplicação** com o banco apagado antes
> — as respostas comentadas no arquivo são as reais, não presumidas.
>
> O roteiro supõe banco limpo e execução na ordem: os ids dos planos (1 a 12)
> são atribuídos na criação. Os planos 1001 a 1004 vêm da carga inicial e não
> atrapalham — o seed deixa a R1 livre e não usa vaga em FLN, que são os
> recursos do caminho feliz.
>
> A seção 8 fecha com o **teste de extensibilidade do §2**: cadastrar um
> aeroporto e uma aerovia novos e usá-los num plano aprovado, sem reiniciar a
> aplicação.

O formato é o mesmo do `testRequests.http` do exemplo da biblioteca, em
`PROJAQ/Exercícios e Trabalhos/SemanaDia31-08/ex1-biblioteca-v3-rotasComParametros/`.

A documentação formal da API é gerada por OpenAPI (springdoc), e o Swagger UI
serve como ferramenta de demonstração.
