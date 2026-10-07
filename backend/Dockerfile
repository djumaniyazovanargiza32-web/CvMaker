# ==========================================
# Multi-stage Dockerfile for AI CV Maker Backend
# ==========================================

# Stage 1: Build the distribution
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /home/gradle/project

# Copy Gradle wrapper and configuration files first for Docker layer caching
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY backend/build.gradle.kts ./backend/

RUN chmod +x gradlew

# Download dependencies / warm cache
RUN ./gradlew :backend:dependencies --no-daemon || true

# Copy backend source code
COPY backend/src ./backend/src

# Build distribution package
RUN ./gradlew :backend:installDist --no-daemon

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add curl for container health check
RUN apk add --no-cache curl

# Copy the unpacked distribution
COPY --from=builder /home/gradle/project/backend/build/install/backend /app

# Set default configuration
ENV PORT=8080
ENV GROQ_MODEL=llama-3.3-70b-versatile
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:${PORT}/health || exit 1

ENTRYPOINT ["/app/bin/backend"]
