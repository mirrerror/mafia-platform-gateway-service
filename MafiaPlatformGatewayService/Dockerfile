# Stage 1: Build the application using Maven
FROM maven:3.9.8-eclipse-temurin-17 AS builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests


# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Run the application directly
ENTRYPOINT ["java", "-jar", "app.jar"]