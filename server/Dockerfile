FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/northstar-metrics-api-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
