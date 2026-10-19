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
> são atribuídos na criação. Os planos 1001 a 1005 vêm da carga inicial e não
> atrapalham — o seed deixa a R1 livre e não usa vaga em FLN, que são os
> recursos do caminho feliz.
>
> A seção 8 fecha com o **teste de extensibilidade do §2**: cadastrar um
> aeroporto e uma aerovia novos e usá-los num plano aprovado, sem reiniciar a
> aplicação.

O formato é o do REST Client do VS Code, que também abre no IntelliJ. Quem
preferir linha de comando pode traduzir cada bloco para `curl`: método, rota,
cabeçalho e corpo estão explícitos.

A documentação formal da API é gerada por OpenAPI (springdoc), e o Swagger UI
serve como ferramenta de demonstração.
