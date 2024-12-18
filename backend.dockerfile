# Stage 1.1: Build the backend Spring Boot application
FROM maven:3.8.4-amazoncorretto-17 AS build

# Set the working directory for the backend
WORKDIR /app

COPY pom.xml .
COPY src/ ./src

# Build the backend application
RUN mvn clean package -DskipTests

# Stage 1.2: Create lightweight runtime image
FROM amazoncorretto:17
WORKDIR /app

# Copy the built JAR file from the backend build stage to the final image
COPY --from=build /app/target/*.jar app.jar

# Expose the backend application port
EXPOSE 8080

# Run the backend application
CMD ["java", "-jar", "app.jar"]