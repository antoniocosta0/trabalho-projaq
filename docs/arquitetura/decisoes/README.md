# Decisões arquiteturais (ADR)

Um arquivo por decisão. O enunciado exige, para cada decisão relevante: a decisão adotada, as
principais alternativas consideradas e a justificativa da escolha.

## Da parte 1 (monólito)

| ADR | Decisão |
|---|---|
| [0001](0001-estilo-arquitetural-hexagonal.md) | Estilo arquitetural (hexagonal × camadas) |
| [0002](0002-entidade-de-dominio-separada-da-de-persistencia.md) | Entidade de domínio separada da entidade de persistência |
| [0003](0003-banco-h2-em-arquivo.md) | Banco H2 em arquivo (não em memória) |
| [0004](0004-atomicidade-da-aprovacao.md) | Atomicidade da aprovação |
| [0005](0005-slot-como-value-object.md) | `Slot` como value object |
| [0006](0006-regra-de-operacao-polimorfica.md) | Extensão por categoria de aeronave sem alterar a validação |
| [0007](0007-heranca-de-aeronave-em-tabela-unica.md) | Herança de aeronave em tabela única |
| [0008](0008-interpretacoes-do-enunciado.md) | Interpretações do enunciado |
| [0016](0016-idioma-do-codigo.md) | Idioma do código: domínio em português, infraestrutura em inglês |
| [0017](0017-cadastros-fora-da-lista-de-casos-de-uso.md) | Cadastros fora da lista de casos de uso |

## Da parte 2 (microsserviços)

| ADR | Decisão | Etapa |
|---|---|---|
| [0009](0009-implantacao-em-conteineres.md) | Um serviço por contêiner, com Docker Compose | preparação |
| [0010](0010-malha-aerea-por-porta-e-copia-no-trecho.md) | Malha aérea por porta, cópia da aerovia em cada trecho | 1 |
| [0011](0011-outbox-de-aprovacoes.md) | Outbox transacional das aprovações | 1 / 4 |
| [0012](0012-propriedade-dos-planos.md) | Propriedade dos planos (`X-Usuario`, `criador`) | 1 |
| [0013](0013-gateway-e-registro-de-servicos.md) | Gateway e registro de serviços (Eureka) | 2 |
| [0014](0014-cadastro-por-http.md) | Cadastro por HTTP síncrono | 3 |
| [0015](0015-registro-assincrono-de-aprovacoes.md) | Registro assíncrono das aprovações (RabbitMQ) | 4 |

As ADRs 0016 e 0017 ficaram sem registro formal na parte 1 (só uma menção em
`docs/padroes-desenvolvimento.md`); foram escritas nesta auditoria porque continuam valendo na
parte 2, estendidas aos novos serviços — ver `docs/AUDITORIA-PARTE1.md`.
