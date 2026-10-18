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
> O roteiro supõe banco limpo e execução na ordem: os ids dos planos (1 a 10)
> são atribuídos na criação. A única seção ainda comentada é a dos cadastros,
> que depende da frente D1.

O formato é o mesmo do `testRequests.http` do exemplo da biblioteca, em
`PROJAQ/Exercícios e Trabalhos/SemanaDia31-08/ex1-biblioteca-v3-rotasComParametros/`.

A documentação formal da API é gerada por OpenAPI (springdoc), e o Swagger UI
serve como ferramenta de demonstração.
