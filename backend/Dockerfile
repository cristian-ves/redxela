# Build stage: compiles the application with Maven.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -B -DskipTests package

# Run stage: only the JRE and the jar, as an unprivileged user.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home redxela
COPY --from=build /app/target/*.jar app.jar
USER redxela
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
