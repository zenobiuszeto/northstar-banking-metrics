FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl && addgroup -S northstar && adduser -S northstar -G northstar
WORKDIR /app
COPY --from=build --chown=northstar:northstar /app/build/libs/northstar-metrics-api-1.0.0.jar app.jar
USER northstar
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=5 CMD curl -fsS http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
