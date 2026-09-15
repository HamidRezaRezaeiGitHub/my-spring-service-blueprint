# Multi-stage build: produce a slim runtime image with the packaged JAR.
#
# Build:
#   docker build -t application:latest .
#
# Run (standalone, against a Postgres reachable on the host network):
#   docker run --rm -p 8080:8080 \
#     -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/application \
#     -e DB_USERNAME=application -e DB_PASSWORD=application \
#     application:latest
#
# Run (full stack via docker compose):
#   docker compose --profile app up -d
#
# NOTE: this image is intentionally credential-free. Mount Firebase / DB
# secrets at runtime via env vars or volumes; never bake them into the image.

# ---- Stage 1: build ----------------------------------------------------------
# Uses the floating 3.9 tag so future Maven patch releases (security fixes)
# are picked up automatically without requiring a Dockerfile edit.
FROM maven:3-eclipse-temurin-24 AS build
# The official Maven image sets MAVEN_CONFIG="/root/.m2". The Spring Boot
# mvnw script appends $MAVEN_CONFIG verbatim to the Maven command line, which
# causes "Unknown lifecycle phase '/root/.m2'" errors. Clear it so the wrapper
# does not inject a spurious positional argument.
ENV MAVEN_CONFIG=""
WORKDIR /workspace

# The script-only Maven wrapper falls back from the configured ZIP to a tarball
# when unzip is absent. Install unzip so the pinned ZIP checksum is verified.
RUN apt-get update \
 && apt-get install --yes --no-install-recommends unzip \
 && rm -rf /var/lib/apt/lists/*

# Cache dependencies — copy POM first so changes to source don't bust the cache.
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw -B -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -q -DskipTests package \
 && find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' -exec cp '{}' app.jar \; \
 && test -s app.jar

# ---- Stage 2: runtime --------------------------------------------------------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Run as non-root for safety.
RUN useradd --system --uid 1001 --shell /usr/sbin/nologin application
USER application

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

# Use exec form + sh so $JAVA_OPTS expands.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
