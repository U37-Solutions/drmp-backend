FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY --from=build /app/target/drmp-0.0.1-SNAPSHOT.jar drmp-backend.jar

CMD ["java", "-jar", "drmp-backend.jar"]
