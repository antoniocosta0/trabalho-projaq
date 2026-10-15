# Logs de trabalho — `trabalho-1`

Cada pessoa mantém **os seus próprios** `log.md` e `decisoes.md` nesta pasta.
Eles são **locais**: não vão para o GitHub, não geram conflito de merge e não
aparecem no repositório de ninguém além do dono. No fim, a equipe compara os
três e consolida o que vai para a apresentação e para os ADRs.

Os dois arquivos estão no `.gitignore`. Se não existirem na tua máquina, cria
com o formato abaixo — é só copiar.

## `logs/log.md` — o que foi **feito**

Append-only: nunca reescreve nem apaga entrada antiga; entrada errada é
corrigida por uma nova, apontando a correção. Formato de cada entrada
(ver `CLAUDE.md`, seção 9.2):

```markdown
## AAAA-MM-DD — título curto

**Resumo.** O que foi feito, em 2 a 5 linhas.

**Casos de uso tocados.** UCnn (nome) — estado.

**Arquivos tocados.**
- `caminho/arquivo` — o que mudou

**Verificações feitas.**
- `./mvnw clean test` — N testes, resultado
- requisições manuais: quais e o resultado
- o que NÃO foi testado

**Commits.**
- `hash` — mensagem

**Pendências.**
- O que ficou aberto e o próximo passo
```

## `logs/decisoes.md` — o que foi **decidido** e por quê

Append-only. É o rascunho dos ADRs de `docs/arquitetura/decisoes/`: toda decisão
fechada aqui vira um ADR lá. O campo mais valioso é **"Decisões" com a
alternativa descartada** — é ele que responde o professor quando pergunta "por
que não fez do outro jeito?". Formato (ver `CLAUDE.md`, seção 9.3):

```markdown
## AAAA-MM-DD — título curto

**Assunto.** Do que se tratou, em 1 ou 2 linhas.

**Decisões.**
- O que ficou decidido e por quê. Alternativa descartada e o motivo.

**Em aberto.**
- Pergunta sem resposta, e do que ela depende.

**Achados técnicos.**
- Fato verificado, com a referência (arquivo e seção).

**Retomar por aqui.**
- O próximo passo, escrito para ser lido dias depois sem contexto.
```

## Na hora de consolidar

O que vira entrega pública sai daqui e vai para `docs/`:

- decisão fechada → um ADR em `docs/arquitetura/decisoes/`
- o que foi feito → o roteiro da apresentação e o `README.md` da raiz
