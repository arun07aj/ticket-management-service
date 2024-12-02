# Stage 1: Build the Spring Boot application
FROM maven:3.8.4-amazoncorretto-17 AS build

# Set the working directory for the backend
WORKDIR /app

# Build the backend application
RUN mvn clean package -DskipTests

# Copy the built JAR file from the backend build stage to the final image
COPY --from=build /app/target/*.jar app.jar

# Expose backend server port
EXPOSE 8080

# Run the Spring Boot application with the built frontend
CMD ["java", "-jar", "app.jar"]