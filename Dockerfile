# STAGE 1: Build stage using Maven with Eclipse Temurin Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Package the application (skip tests for faster demo builds)
RUN mvn clean package -DskipTests

# STAGE 2: Runtime stage using a lightweight JRE from Eclipse Temurin
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for RSA keys
RUN mkdir -p /app/secrets

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]