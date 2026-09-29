# ============================================================
# Stage 1 — Build
# Maven + JDK 17: compile sources and produce the fat JAR.
# ============================================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy dependency descriptor first for better layer caching.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source tree and build — skip tests here; they need a live DB.
COPY src ./src
# Remove application.properties so it is NOT bundled into the JAR.
# Inside the Docker container the app must use env-var configuration
# (DB_URL / DB_USERNAME / DB_PASSWORD), not a localhost properties file.
RUN rm -f src/main/resources/application.properties
RUN mvn clean package -DskipTests -q

# ============================================================
# Stage 2 — Runtime
# Lightweight JRE 17 only; no Maven, no JDK in the final image.
# ============================================================
FROM eclipse-temurin:17-jre

LABEL maintainer="Srikarthik K"
LABEL description="Employee Management System — Java 17 / MySQL CLI application"

WORKDIR /app

# Copy the fat JAR produced by the build stage.
COPY --from=builder /build/target/employee-management-system-1.0.0-jar-with-dependencies.jar app.jar

# The application is interactive (CLI); keep stdin open.
# Environment variables DB_URL, DB_USERNAME, DB_PASSWORD must be
# supplied at runtime (via docker-compose.yml or --env-file).
ENTRYPOINT ["java", "-jar", "app.jar"]
