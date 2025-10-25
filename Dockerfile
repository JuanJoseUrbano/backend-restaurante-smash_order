# Usa una imagen base de OpenJDK 21, que coincide con la versión de compilación del proyecto
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR compilado desde la carpeta target al contenedor
COPY target/*.jar app.jar

# El comando para ejecutar la aplicación. El perfil de Spring se pasará en el `docker run`
ENTRYPOINT ["java", "-jar", "app.jar"]