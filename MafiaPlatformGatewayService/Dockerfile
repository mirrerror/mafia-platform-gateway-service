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

# Install netcat
# The rm command at the end is a best practice to keep the image smaller
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Copy wait-for script into container
COPY wait-for.sh /wait-for.sh

RUN sed -i 's/\r$//' /wait-for.sh

RUN chmod +x /wait-for.sh

EXPOSE 8080

# Use wait-for.sh as entrypoint
ENTRYPOINT ["/wait-for.sh", "communication-service", "8080", "java", "-jar", "app.jar"]