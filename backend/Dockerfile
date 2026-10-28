# Один Dockerfile на все Java-сервисы: сначала сборка, потом по образу на каждый сервис (--target)

FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
COPY libs libs
COPY orchestrator orchestrator
COPY validator validator
COPY writer writer
RUN ./gradlew --no-daemon :orchestrator:shadowJar :validator:bootJar :writer:bootJar

FROM eclipse-temurin:25-jre AS validator
COPY --from=build /app/validator/build/libs/*.jar /app/app.jar
EXPOSE 7001
CMD ["java", "-jar", "/app/app.jar"]

FROM eclipse-temurin:25-jre AS writer
COPY --from=build /app/writer/build/libs/*.jar /app/app.jar
EXPOSE 7002
CMD ["java", "-jar", "/app/app.jar"]

FROM eclipse-temurin:25-jre AS orchestrator
COPY --from=build /app/orchestrator/build/libs/*-all.jar /app/app.jar
EXPOSE 24001
CMD ["java", "-DFCGI_PORT=24001", "-jar", "/app/app.jar"]
