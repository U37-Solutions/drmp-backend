FROM maven:3.9.3-eclipse-temurin-17-alpine AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS layers
WORKDIR /layers
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=layers /layers/dependencies/ ./dependencies/
COPY --from=layers /layers/spring-boot-loader/ ./spring-boot-loader/
COPY --from=layers /layers/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=layers /layers/application/ ./application/

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
