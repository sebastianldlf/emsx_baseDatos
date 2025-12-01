# ===========================
# 1) Etapa de construcción
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom.xml y resolvemos dependencias
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

# Copiamos el código fuente
COPY src ./src

# Construimos el proyecto sin ejecutar tests
RUN mvn clean package -DskipTests


# ===========================
# 2) Etapa final (RUN)
# ===========================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el JAR generado en la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 9090

# Variables de entorno opcionales
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
