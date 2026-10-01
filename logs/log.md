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
- `d1ef0e4` — chore: set up repository with guide, standards and work logs

**Pendências.**
- Criar o projeto Spring Boot em `backend/`, com a árvore de pacotes da seção 8.
- Fechar o modelo de domínio em UML e os casos de uso para a entrega de 05/10.
- Escrever os ADRs 0001 (estilo arquitetural), 0002 (separação domínio ×
  persistência) e 0008 (idioma do código).

## 2026-09-30 — Estrutura do projeto e contexto autocontido

**Resumo.** Criada a árvore de pacotes do backend no formato da arquitetura
hexagonal, com `pom.xml`, wrapper do Maven, classe de aplicação e
`application.properties` apontando para H2 **em arquivo**. O projeto compila.
Criado também o `CONTEXT_NEW.md`: versão autocontida deste guia, sem nenhuma
dependência de caminho local, com a contextualização do conteúdo da cadeira
escrita por extenso — para a equipe e para ferramentas de IA que não têm o
material baixado.

**Casos de uso tocados.** Nenhum — só estrutura.

**Arquivos tocados.**
- `backend/` — árvore completa: `domain/{model,servicos,ports/in,ports/out}` e
  `infra/{adapters/in/rest,adapters/out/persistence,config}`, com `.gitkeep`
- `backend/pom.xml` — Spring Boot 4.1.1, Java 25, web + validation + data-jpa +
  H2 + test; springdoc comentado, à espera da versão compatível
- `backend/README.md` — o que vai em cada pasta e a regra de dependência
- `backend/src/main/resources/application.properties` — H2 em arquivo,
  `ddl-auto=none`, schema e data por script
- `backend/src/main/resources/db/{schema.sql,data.sql}` — só comentários com as
  exigências; o conteúdo ainda não existe
- `docs/arquitetura/README.md` — o que cada nível do C4 mostra
- `CONTEXT_NEW.md` — 1498 linhas, autocontido
- `CLAUDE.md` — seção 4 atualizada com o estado real da árvore
- `.gitignore` — corrigido: `maven-wrapper.properties` precisa ser versionado
- `docs/padroes-desenvolvimento.md` — pacote do `...Service` corrigido para
  `domain.servicos`

**Verificações feitas.**
- `./mvnw clean compile` — sucesso (os avisos de `jansi` e `guava` são do
  próprio Maven, não do projeto).
- `grep` em `CONTEXT_NEW.md` por caminhos locais — nenhum vazamento.
- NÃO testado: `./mvnw spring-boot:run`, porque `schema.sql` está vazio e a
  aplicação ainda não tem nada para servir.

**Commits.**
- `979bd05` — feat: add spring boot project skeleton with hexagonal package structure

**Pendências.**
- Escrever `schema.sql` e `data.sql`, e definir a data de referência.
- Descomentar o springdoc com a versão compatível com o Spring Boot 4.
- Confirmar o `groupId`/pacote `com.pucrs` com a equipe.
- Fechar o modelo de domínio em UML e os casos de uso para a entrega de 05/10.
