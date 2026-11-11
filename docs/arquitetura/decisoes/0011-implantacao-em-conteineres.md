# ADR 0011 — Implantação: um serviço por contêiner, orquestrado por Docker Compose

**Data:** 2026-11-11
**Situação:** aceita

## Contexto

A parte 2 do trabalho evolui o monólito para microsserviços: backend de gestão
de planos, cadastro da rede aérea, registro de aprovações, e a infraestrutura
ao redor (API Gateway, Eureka, RabbitMQ), cada aplicação com seu próprio banco
(enunciado da parte 2, §2 e §3). Exige também que "o ambiente deverá ser
reproduzível a partir do repositório" e que a forma de execução e a sequência
de inicialização sejam documentadas (§6).

Antes de extrair qualquer serviço, é preciso decidir **como cada aplicação é
empacotada e implantada** — o problema que o material da cadeira formula como
"como os microsserviços devem ser empacotados e implantados?"
(`MS_P4_ModelosDeImplantacao`). As forças listadas lá valem aqui: cada
instância precisa de isolamento e limite de CPU e memória; construir e
implantar tem que ser verificável e repetível; múltiplas instâncias exigem
descoberta; e rede, configuração e dados também fazem parte da implantação.

## Alternativas consideradas

1. **Processos na máquina do desenvolvedor (`./mvnw spring-boot:run` em cada
   pasta)** — é como a parte 1 roda. Sem custo novo, mas com cinco ou seis
   aplicações a sequência de inicialização vira um roteiro manual, as portas
   colidem, o RabbitMQ precisa ser instalado à parte e "funciona na minha
   máquina" deixa de ser reprodutível. Descartada.
2. **Um serviço por VM** (`microservices.io/patterns/deployment/service-per-vm`)
   — isolamento forte, mas cada VM carrega um sistema operacional inteiro:
   inicialização lenta e custo de memória desproporcional para uma
   demonstração em notebook. Descartada.
3. **Serverless** — não se aplica: Eureka, RabbitMQ e o H2 em arquivo pedem
   processos de longa duração com estado em disco.
4. **Um serviço por contêiner, com Docker Compose** — cada aplicação vira uma
   imagem; cada instância, um contêiner. O `compose.yaml` declara serviços,
   rede, volumes e configuração num arquivo versionado. Escolhida.
5. **Kubernetes** — resolve o mesmo problema com mais recursos (autoescala,
   autorrecuperação), mas com custo operacional que um trabalho de disciplina
   não justifica. O Compose cobre o que o enunciado pede.

## Decisão

Cada aplicação Spring Boot é empacotada como **imagem própria**, construída por
um **Dockerfile em dois estágios**, e o conjunto é descrito num **`compose.yaml`
na raiz do repositório**. Nesta etapa existe um único serviço, o backend atual
(`planos`); os próximos entram como novos blocos no mesmo arquivo.

Como cada imagem é construída (`backend/Dockerfile`):

| Item | Escolha | Por quê |
|---|---|---|
| Estágio `build` | `eclipse-temurin:25.0.4.1_1-jdk-noble` + Maven Wrapper | compila **e roda os testes**: imagem que não passa na suíte não é gerada |
| Estágio `runtime` | `eclipse-temurin:25.0.4.1_1-jre-noble` + `app.jar` | a imagem final não leva fontes, compilador nem repositório Maven |
| Cache | `pom.xml` e wrapper copiados antes de `src/` | mudar código não baixa as dependências de novo |
| Usuário | UID 10001, sem privilégios | o processo não roda como root dentro do contêiner |
| Tag da base | explícita, nunca `latest` | a base é identificada; atualizar é reconstruir e retestar |
| Memória | `-XX:MaxRAMPercentage=75.0` | o heap é só parte do processo; sobra folga dentro do limite |

Como o contêiner roda (`compose.yaml`):

| Item | Escolha | Por quê |
|---|---|---|
| Dados | volume nomeado `planos-data` em `/app/data` | o H2 já é **em arquivo** (ADR 0003); o volume é o que faz o arquivo sobreviver à recriação do contêiner |
| Porta | `127.0.0.1:8080` | só no loopback; quando o gateway existir, só ele publica porta |
| Rede | bridge própria `planos-net` | o DNS do Docker resolve os serviços pelo nome; é onde Eureka, gateway e RabbitMQ vão entrar |
| Saúde | `curl /actuator/health` dentro do contêiner | processo ativo não é aplicação pronta; `--wait` espera o `healthy` |
| Limites | 1 CPU, 512 MB | isolamento de recursos entre os serviços que virão |
| Nome | sem `container_name` fixo | permite `--scale` quando houver descoberta |

## Consequências

**Facilita.**
- O ambiente inteiro sobe com um comando (`docker compose up -d --build --wait`),
  sem Java instalado na máquina de quem avalia.
- Cada novo serviço da parte 2 é um bloco a mais no `compose.yaml`, com sua
  própria imagem e seu próprio volume — o que já materializa a regra de
  propriedade dos dados (cada serviço, seu banco).
- O build da imagem é também uma execução da suíte de testes.

**Custa.**
- Docker passa a ser pré-requisito da forma recomendada de execução. O
  `./mvnw spring-boot:run` continua funcionando para desenvolvimento.
- O primeiro build baixa dependências e roda os 147+ testes: leva alguns
  minutos.
- O backend ganhou o `spring-boot-starter-actuator`, só para expor
  `/actuator/health` (os demais endpoints do actuator ficam fechados).

**Passa a ser obrigatório.**
- Todo serviço novo tem Dockerfile em dois estágios, usuário sem privilégios,
  healthcheck e, se tiver banco, volume nomeado próprio.
- `docker compose down -v` **apaga** os dados; `down` preserva. A diferença tem
  que estar no README.
- `healthy` não prova que o fluxo de negócio funciona: a verificação de cada
  etapa continua sendo por requisição real (`docs/api/requests.http`).
