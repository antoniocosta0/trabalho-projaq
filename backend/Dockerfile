# syntax=docker/dockerfile:1
#
# Imagem do backend de gestao de planos de voo.
# Build em dois estagios (MS_P4, "Build em multiplos estagios"):
#   1. build   - JDK 25 + Maven Wrapper: baixa dependencias, compila, TESTA e empacota.
#   2. runtime - JRE 25 + app.jar, executando como usuario sem privilegios (UID 10001).
# A imagem final nao leva fontes, compilador nem o repositorio Maven.

# ---------------------------------------------------------------------------
# Estagio 1: build
# ---------------------------------------------------------------------------
FROM eclipse-temurin:25.0.4.1_1-jdk-noble AS build
WORKDIR /workspace/app

# POM e wrapper antes dos fontes: enquanto o pom nao mudar, a camada com as
# dependencias baixadas vem do cache e o rebuild so recompila o codigo.
COPY mvnw pom.xml ./
COPY .mvn .mvn
# sed: checkout no Windows grava o mvnw com CRLF, e o sh do Linux nao o executa.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
# Os testes rodam aqui: imagem que nao passa na suite nao e gerada.
RUN ./mvnw -B clean package \
 && cp target/planos-de-voo-*.jar target/app.jar

# ---------------------------------------------------------------------------
# Estagio 2: runtime
# ---------------------------------------------------------------------------
FROM eclipse-temurin:25.0.4.1_1-jre-noble AS runtime

# curl e usado pelo healthcheck do compose.yaml (GET /actuator/health).
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Usuario sem privilegios. /app/data e criada com o dono certo antes do
# VOLUME: um volume nomeado vazio herda o dono da pasta da imagem, e o H2
# precisa escrever nela.
RUN groupadd --system --gid 10001 app \
 && useradd --system --uid 10001 --gid app --no-create-home app \
 && mkdir -p /app/data \
 && chown -R app:app /app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app/target/app.jar ./app.jar

USER 10001

# O H2 grava em ./data/planosdevoo (application.properties), isto e, /app/data.
# Sem volume montado aqui, o estado morre junto com o conteiner.
VOLUME /app/data

# Documenta a porta; quem publica no host e o compose.yaml.
EXPOSE 8080

# O heap e so parte da memoria do processo: 75% do limite do conteiner deixa
# folga para metaspace, threads e buffers.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
