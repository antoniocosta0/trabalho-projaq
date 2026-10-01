# Arquitetura

Entrega de **07/10**: diagramas C4 (contexto, containers, componentes) e
estrutura de pastas.

| Nível | Arquivo | O que mostra |
|---|---|---|
| 1 — Contexto | `c4-contexto.puml` / `.png` | o sistema, os usuários e os sistemas externos |
| 2 — Containers | `c4-containers.puml` / `.png` | aplicação Spring Boot e banco: responsabilidades, tecnologias, comunicação |
| 3 — Componentes | `c4-componentes.puml` / `.png` | organização interna da aplicação: adaptadores, casos de uso, modelo, portas |

Nível 4 (código) não é exigido — o modelo de domínio em UML cobre isso.

A estrutura de pastas correspondente está criada em `backend/`, documentada em
`backend/README.md`. As decisões ficam em `decisoes/`.

**Regra:** os diagramas representam a arquitetura **efetivamente adotada**.
Diagrama que mostra uma porta que o código não tem é erro de entrega.
