# ADR 0003 — H2 em modo arquivo como banco de dados

**Data:** 2026-10-15
**Situação:** aceita

## Contexto

O enunciado exige persistência em banco de dados e é explícito sobre o efeito
que quer: "as alterações realizadas pela API REST deverão persistir após
reinicialização" (§8.2). Exige também que a carga inicial seja "reproduzível a
partir dos artefatos entregues, sem inclusão manual prévia à apresentação", e
que "a solução deverá ser reproduzível a partir do repositório, sem depender de
configurações ou dados exclusivos do ambiente da equipe" (§8.5).

Isso elimina de saída duas opções que seriam convenientes: manter estado em
memória e persistir em arquivo CSV/JSON. Sobra escolher **qual** banco, com uma
restrição de contexto: o professor vai clonar o repositório e rodar na máquina
dele, provavelmente durante a apresentação.

## Alternativas consideradas

1. **H2 em memória (`jdbc:h2:mem:`).** Vantagem: zero configuração, parte em
   milissegundos, perfeito para testes. Desvantagem: o estado morre quando a
   aplicação para. **Viola diretamente** o §8.2. Está registrada aqui porque é
   o default de quase todo tutorial de Spring Boot e seria o erro fácil de
   cometer.

2. **PostgreSQL em container Docker.** Vantagem: é o banco real, aproxima o
   trabalho de um ambiente de produção, e `docker-compose.yml` versionado
   mantém a reprodutibilidade. Desvantagem: adiciona o Docker como
   pré-requisito para rodar o projeto — se a máquina onde a apresentação
   acontecer não tiver Docker funcionando, ou se o download da imagem falhar na
   hora, a demonstração não acontece. Não foi escolhida pelo risco no dia da
   apresentação somado ao custo de configuração a três dias da entrega.

3. **SQLite.** Vantagem: arquivo único, sem servidor, igualmente simples.
   Desvantagem: o suporte a JPA/Hibernate depende de dialeto de terceiros, não
   é o caminho coberto pelo material da cadeira, e não traz nenhuma vantagem
   sobre o H2 neste contexto. Não foi escolhida.

## Decisão

**H2 em modo arquivo**, com a URL
`jdbc:h2:file:./data/planosdevoo;DB_CLOSE_DELAY=-1`. O schema é criado por
`src/main/resources/db/schema.sql` e a carga inicial aplicada por
`db/data.sql`, ambos versionados, com `spring.jpa.hibernate.ddl-auto=none`.

## Consequências

**Facilita.** Rodar o projeto exige apenas Java e o wrapper do Maven que já
está no repositório: `./mvnw spring-boot:run` e pronto, sem Docker, sem serviço
externo, sem credencial. O banco vive em `./data/`, que está no `.gitignore`;
apagar a pasta e subir de novo recria tudo do zero, o que é exatamente o
roteiro de teste da carga inicial. O console web do H2 fica disponível em
`/h2-console` e serve para inspecionar reservas durante a apresentação.

**Custa.** Não é o banco que um sistema real usaria, e algumas diferenças de
dialeto (tipos, `MERGE` em vez de `INSERT ... ON CONFLICT`) amarram os scripts
ao H2. Se o projeto migrar para PostgreSQL, `schema.sql` e `data.sql` precisam
ser revisados — o código Java, não, por causa das ADRs 0001 e 0002.

**Passa a ser obrigatório.**

- `spring.jpa.hibernate.ddl-auto` fica em `none`. Schema gerado pelo Hibernate
  não é versionável nem reproduzível, e o enunciado cobra reprodutibilidade.
- `data.sql` usa `MERGE ... KEY (...)` em vez de `INSERT`, porque o script roda
  a cada inicialização e o banco em arquivo persiste entre elas.
- O `README.md` precisa dizer como apagar o banco e recomeçar do zero.
- O roteiro de verificação da entrega — apagar o banco, subir,
  conferir as contagens, aprovar um plano, **reiniciar** e confirmar que ele
  continua lá — é a prova de que esta decisão atende o §8.2, e precisa rodar
  antes da entrega.
