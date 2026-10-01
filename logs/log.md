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

## 2026-09-30 — Preparação do repositório

**Resumo.** Enunciado e cronograma lidos por inteiro. Material da cadeira
mapeado, com destaque para o estudo de caso de arquitetura hexagonal (`P7a`),
que define a estrutura de pastas adotada. Repositório Git inicializado e
estrutura de documentação, logs e padrões criada. Nenhum código de aplicação
ainda.

**Casos de uso tocados.** Nenhum — UC01 a UC19 apenas identificados e
numerados em `CLAUDE.md` seção 7.

**Arquivos tocados.**
- `CLAUDE.md` — guia de contexto do repositório, criado
- `.gitignore` — Java, Maven, banco local, IDE, macOS
- `docs/padroes-desenvolvimento.md` — padrões de branch, commit, PR e nomenclatura Java
- `.github/pull_request_template.md` — template de PR adaptado para Java e hexagonal
- `logs/log.md`, `logs/decisoes.md` — criados com cabeçalho
- `docs/arquitetura/decisoes/0000-template.md` — formato de ADR
- `README.md` — esqueleto com as seções exigidas pelo enunciado

**Verificações feitas.**
- Cronograma conferido contra `PAS_TF_2026_2_CronogramaPlanosDeVoo_P1.pdf`.
- Estrutura de pastas da hexagonal conferida contra `P7a_EstudoDeCasoArquiteturaHexagonal.pdf`.
- Versões (Spring Boot 4.1.1, Java 25) conferidas no `pom.xml` do exemplo da
  biblioteca, em `SemanaDia31-08/ex1-biblioteca-v3-rotasComParametros/`.
- NÃO testado: nada executável existe ainda.

**Commits.**
- (preencher após o commit inicial)

**Pendências.**
- Criar o projeto Spring Boot em `backend/`, com a árvore de pacotes da seção 8.
- Fechar o modelo de domínio em UML e os casos de uso para a entrega de 05/10.
- Escrever os ADRs 0001 (estilo arquitetural), 0002 (separação domínio ×
  persistência) e 0008 (idioma do código).
