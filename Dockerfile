# ================================
# Multi-stage build para FACTURAaaS
# ================================

# Etapa 1: Construcción
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

# Copiar archivos de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias (cacheadas si no cambia pom.xml)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Construir aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
