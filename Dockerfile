# Builder: usa OpenJDK 21 y el Maven Wrapper presente en el repo para evitar depender de tags maven específicos
FROM openjdk:21-jdk-slim AS builder
WORKDIR /build

# Copiamos mvnw y la carpeta .mvn primero para usar el wrapper (y acelerar cache)
COPY mvnw mvnw
COPY .mvn .mvn
# Copiamos el pom para que el layer de dependencias quede cacheado
COPY pom.xml pom.xml

# Hacemos ejecutable el wrapper e instalamos dependencias offline
RUN chmod +x mvnw \
    && ./mvnw -B -DskipTests dependency:go-offline

# Copiamos el código fuente y empacamos
COPY src src
RUN ./mvnw -B -DskipTests package

# Runtime: imagen ligera (aquí usamos openjdk:21-jdk-slim para garantizar compatibilidad con Java 21)
FROM openjdk:21-jdk-slim AS runtime
LABEL maintainer="tu-email@ejemplo.com" \
      org.opencontainers.image.source="https://github.com/tu-repo"

ARG JAR_FILE=target/*.jar
WORKDIR /app

# Copiamos el jar empaquetado desde el builder
COPY --from=builder /build/target/*.jar /app/app.jar

# Crear un usuario no-root y ajustar permisos
RUN useradd -r -s /usr/sbin/nologin app \
    && chown -R app:app /app
USER app

# Puerto por defecto de Spring Boot
EXPOSE 8082

# Valores por defecto para la JVM (se pueden sobrescribir con -e JAVA_OPTS="...")
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# ENTRYPOINT permite añadir opciones por entorno
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]