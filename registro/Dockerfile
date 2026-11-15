# syntax=docker/dockerfile:1
FROM eclipse-temurin:25.0.4.1_1-jdk-noble AS build
WORKDIR /workspace/app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B clean package \
 && cp target/registro-*.jar target/app.jar

FROM eclipse-temurin:25.0.4.1_1-jre-noble AS runtime

# curl para o healthcheck do compose.yaml
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

RUN groupadd --system --gid 10001 app \
 && useradd --system --uid 10001 --gid app --no-create-home app \
 && mkdir -p /app/data \
 && chown -R app:app /app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app/target/app.jar ./app.jar

USER 10001

VOLUME /app/data

EXPOSE 8083

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
