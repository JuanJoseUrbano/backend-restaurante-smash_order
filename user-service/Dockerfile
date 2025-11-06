# Stage 1: Build the specific microservice
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the parent pom.xml
COPY pom.xml .

# Copy the user-service module files
COPY user-service/pom.xml ./user-service/
COPY user-service/src ./user-service/src

# Build only the user-service module and its dependencies
RUN mvn -pl user-service -am clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from the build stage
COPY --from=build /app/user-service/target/*.jar app.jar

# Expose the port for the user service (e.g., 8081)
EXPOSE 8081

# Health check for the user service
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
