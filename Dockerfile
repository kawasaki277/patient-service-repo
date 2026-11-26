# Contexto de build = la carpeta de ESTE servicio (su propio repo, o "Root Directory" en Render).
# Ya no se asume un monorepo con pom padre ni con los otros modulos.

# ---------- Etapa 1: compilar ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# pom primero: si solo cambia el codigo, Docker reutiliza esta capa de dependencias
# (mejor esfuerzo: el "|| true" evita que un fallo de cache rompa el build real de abajo).
COPY pom.xml .
RUN mvn -B -ntp -q dependency:resolve-plugins dependency:resolve || true

COPY src ./src
# maven.test.skip: no compila ni ejecuta tests en el deploy
RUN mvn -B -ntp -Dmaven.test.skip=true clean package

# ---------- Etapa 2: imagen de ejecucion ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --uid 1001 appuser
COPY --from=build /workspace/target/app.jar app.jar
USER appuser

# Render inyecta PORT (10000 por defecto); application.yml lo lee con ${PORT:8081}
# 60% de RAM para heap: deja margen para metaspace/hilos y evita el OOM-kill del plan de 512 MB.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:+UseSerialGC -Xss512k -XX:+ExitOnOutOfMemoryError"
EXPOSE 10000
ENTRYPOINT ["java","-jar","app.jar"]
