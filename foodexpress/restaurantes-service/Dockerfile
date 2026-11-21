# Etapa 1 (build): JDK 21 completo + Maven para compilar y empaquetar el JAR
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2 (runtime): solo el JRE 21, más liviano; el JDK y Maven no llegan a la imagen final
FROM eclipse-temurin:21-jre
# curl lo usa el health check de docker-compose (GET /actuator/health)
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 1001 app
WORKDIR /app
COPY --from=build /app/target/restaurantes-service.jar app.jar
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
