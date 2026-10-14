# Log de trabalho — `trabalho-1`

O que foi **feito**. Append-only: nunca reescreve nem apaga entrada antiga;
entrada errada é corrigida por uma nova, apontando a correção.

Formato de cada entrada (ver `CLAUDE.md`, seção 9.2):

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

---
