# ADR 0009 — Um serviço por contêiner, com Docker Compose

**Decisão.** Cada aplicação tem um Dockerfile em dois estágios (build com JDK e testes, runtime
só com JRE e o jar, usuário sem privilégios). O `compose.yaml` na raiz sobe todos os serviços
numa rede própria, cada banco num volume nomeado. Só o ponto de entrada publica porta no host.

**Alternativas.**
- `./mvnw spring-boot:run` em cada pasta: sem custo novo, mas com várias aplicações e o
  RabbitMQ a inicialização vira um roteiro manual e deixa de ser reproduzível.
- Kubernetes: resolve o mesmo problema com custo operacional que o trabalho não justifica.

**Justificativa.** O enunciado pede um ambiente reproduzível a partir do repositório. Com o
Compose o sistema inteiro sobe com um comando e sem Java instalado; parar e retomar um serviço
(`docker compose stop/start`) serve às demonstrações de falha. O `./mvnw` continua valendo para
desenvolvimento. O backend ganhou o actuator só para o healthcheck (`/actuator/health`).
