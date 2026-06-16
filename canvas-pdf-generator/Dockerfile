# ── Estágio 1: Build com Maven ──────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências (cache de camada)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o código-fonte e compila o JAR
COPY src ./src
RUN mvn package -DskipTests -q

# ── Estágio 2: Runtime com JRE ───────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Porta exposta pela aplicação Spring Boot
EXPOSE 8080

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
