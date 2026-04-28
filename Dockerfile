# Stage 1: Build
FROM eclipse-temurin:26-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:26-jdk AS runtime

WORKDIR /app

RUN mkdir -p data

COPY --from=build /app/build/libs/cash-management-back-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8081

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]