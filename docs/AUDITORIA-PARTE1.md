# Auditoria do código (parte 1 → parte 2)

Revisão do estado atual do repositório (branch `main`, após as etapas 1 a 4 já implementadas
pelo Vicenzo) contra o enunciado da parte 1, o enunciado da parte 2 (`docs/enunciado-parte2.md`)
e as boas práticas de arquitetura hexagonal, segurança e organização do projeto.

**Resultado geral: código em ótimo estado.** As 4 etapas já têm implementação completa, com
testes, ADRs e documentação. Esta auditoria encontrou poucos problemas reais — nenhum deles de
regra de negócio ou de contrato de API — e confirmou, lendo o código, uma série de pontos que já
pareciam certos pela documentação.

## Build e testes (checado nesta sessão)

```
eureka:   compila, 0 testes (esperado: sem regra própria)
gateway:  compila, 0 testes (esperado: só roteamento)
backend:  compila, 62 testes, todos passando
cadastro: compila, 4 testes, todos passando
registro: compila, 2 testes, todos passando
```

`./mvnw test` executado módulo a módulo, online (o modo offline falha por falta de cache local do
Spring Cloud — não é um problema do projeto). **Não validado nesta sessão:** a stack completa via
`docker compose up` — o daemon do Docker não estava disponível no ambiente usado para a auditoria.
Fica como pendência de verificação manual antes da apresentação (ver `docs/PLANO-PARTE2.md`).

## O que foi corrigido

| Commit | O quê | Por quê |
|---|---|---|
| `347dd8f` | `.gitignore` não ignora mais `CLAUDE.md` | o arquivo estava na lista de "guias locais e notas internas" de uma sessão anterior, o que impediria versionar o guia do projeto pedido nesta entrega |

Não foi encontrado nenhum outro problema pequeno, seguro e reversível de corrigir sem tocar em
regra de negócio ou contrato de API. Isso é o esperado para um código já revisado por ADR a cada
decisão relevante.

## O que foi verificado e está correto

- **Separação de camadas.** `domain/` de nenhum dos três serviços de negócio (`backend`,
  `cadastro`, `registro`) importa Spring, JPA ou `infra` — confirmado por leitura e pela regra do
  CI (`.github/workflows/ci.yml`), que falha o build se isso acontecer.
- **Limites transacionais.** `aprovar` e `cancelar` em `PlanoDeVooService` usam `TransacaoPort`
  (uma porta, não `@Transactional` direto no domínio) e fazem busca, verificação, mudança de
  estado e gravação de reservas numa única transação. Aprovação continua tudo-ou-nada.
- **Distinção 404 × indisponibilidade.** `MalhaAereaHttpAdapter` trata `404` do cadastro como
  `Optional.empty()` (recurso não existe) e qualquer outra falha (timeout, conexão recusada) como
  `MalhaAereaIndisponivelException` → `503`. Atende à exigência explícita da etapa 3.
- **Timeouts configurados.** `CadastroClientConfig` define timeout de conexão (2s) e leitura (3s)
  para as chamadas HTTP ao cadastro, configuráveis por propriedade.
- **Outbox transacional.** A aprovação grava o evento do outbox na mesma transação
  (`AprovacaoOutboxAdapter`); um `@Scheduled` separado (`AprovacaoRabbitPublisher`) só marca o
  evento como publicado depois da confirmação do broker (`CorrelationData.Confirm`). Distingue
  tentativa de envio de confirmação, como a etapa 4 exige.
- **Idempotência do consumidor.** `RegistroAprovacaoService.registrar` verifica existência antes
  de gravar, e o schema do `registro` tem `evento_id` como chave primária e `plano_id` com
  `UNIQUE` — a idempotência real vem da constraint do banco, não só da checagem em memória (a
  checagem evita o caminho de exceção no caso comum; a constraint cobre a corrida entre duas
  entregas simultâneas, que o `RabbitListener` com concorrência padrão de 1 não deveria produzir,
  mas a constraint é a rede de segurança).
- **Confirmação só após gravação.** `AprovacaoListener.receber` só retorna (e portanto só confirma
  a mensagem) depois de `registros.registrar` ter terminado. Erro de gravação propaga, a mensagem
  não é confirmada e a política de retry (3 tentativas, depois DLQ) assume.
- **Carga inicial.** `cadastro/data.sql` carrega POA/FLN/CWB/GRU e R1–R8 com os mesmos dados da
  parte 1, via `MERGE ... WHEN NOT MATCHED` (reinício não duplica nem sobrescreve o que a API
  cadastrar). `backend/data.sql` ocupa R2, R4, R5, R6, R7 e R8 (6 de 8 = 75%, acima dos 60%
  exigidos) e grava o outbox dos planos da carga com `eventoId` fixo, para que cheguem ao registro
  pela fila sem duplicar a cada subida — exatamente o que a seção 5 do enunciado pede.
- **Regras de cadastro.** `MalhaAereaService` rejeita aeroporto e aerovia com identificador
  repetido (`RegistroDuplicadoException`) e confere a existência de origem e destino antes de
  cadastrar uma aerovia. Aerovia continua imutável (não há `PUT`/`DELETE`).
- **Segurança básica.** Sem segredo hardcoded (senha do H2 vazia, de uso local, arquivo de banco
  fora do Git), sem `catch (Exception e)` genérico, sem `printStackTrace`, sem CORS aberto, sem
  stack trace nas respostas de erro (`server.error.include-stacktrace=never` + `ProblemDetail` em
  todo módulo).
- **Diff da refatoração da etapa 1** (`git diff parte-1 main -- backend/.../domain/`): as mudanças
  no domínio são estruturais (trocar `Aeroporto`/`Aerovia` completos por códigos, adicionar
  `criador` e `pertenceA`) e preservam a lógica de validação — não encontrei alteração de regra de
  negócio escondida numa refatoração.
- **Nenhum arquivo de build versionado.** `git ls-files` não mostra `target/`, `.class` nem
  `.mv.db`; o `.gitignore` já cobria isso corretamente.

## Pendências registradas (não corrigidas — exigem decisão da equipe ou mais trabalho)

Estas não são "pequenas e reversíveis" no sentido do item anterior: mudá-las tem efeito em teste,
comportamento observável ou é trabalho de verificação manual, então ficam como recomendação, com
responsável sugerido em `docs/PLANO-PARTE2.md`.

1. **Sem teste de camada HTTP em `cadastro` e `registro`.** Os dois têm teste de serviço de
   domínio (`MalhaAereaServiceTest`, `RegistroAprovacaoServiceTest`), mas nenhum teste de
   controller ou `@SpringBootTest` que suba o contexto web — diferente de `backend`, que tem
   `SeedTest`, `AprovacaoAtomicaTest` e `MalhaAereaHttpAdapterTest` cobrindo esse nível. O
   enunciado pede "testes dos novos comportamentos de integração" (§6); hoje a cobertura de
   integração dos dois novos serviços é mais fraca que a do backend original.
2. **Console H2 habilitado no backend** (`spring.h2.console.enabled=true`, só no `backend`, sem
   guarda de profile). Não há CORS aberto nem exposição pela gateway, mas é boa prática restringir
   isso a um profile de desenvolvimento antes de considerar a solução "pronta para produção". Não
   toquei nisso porque pode estar sendo usado deliberadamente para inspecionar o banco durante os
   ensaios de demonstração — é uma decisão de equipe, não uma correção óbvia.
3. **Stack completa não testada via Docker Compose nesta sessão** (ver seção de build/testes
   acima). Precisa de uma rodada manual de `docker compose up -d --build --wait` com as 11
   demonstrações do §7 antes da apresentação.
4. **Autenticação é só o cabeçalho `X-Usuario`, sem verificação de identidade real.** Isso já
   valia na parte 1 e o enunciado da parte 2 explicitamente não exige um provedor de identidade
   novo (§4, etapa 2) — não é uma regressão, é um limite aceito e documentado (ADR 0012), só
   registrado aqui para constar que foi considerado e não ignorado.

## Nada suspeito encontrado no histórico do Vicenzo

Os commits de `vicenzorm` entre 11/11 e 15/11 (etapas 1 a 4) foram revisados por `git log` e por
diff contra a tag `parte-1`. São comentados, pequenos, seguem a convenção `tipo: descrição` já
usada no projeto, e cada tag (`etapa-1` a `etapa-4`) corresponde a um ponto de build e teste
passando. Não há arquivo de build versionado, não há mudança de regra de negócio disfarçada de
refactor, e os ADRs 0010 a 0015 documentam exatamente essas decisões.
