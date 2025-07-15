# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests -B

# Stage 2: Create minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/notification-service.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
