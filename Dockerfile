# Stage 1: Build the Spring Boot application
FROM maven:3.8.4-amazoncorretto-17 AS build

# Set the working directory for the backend
WORKDIR /app

# Copy the backend source code
COPY src/ ./src
COPY pom.xml .

# Use secrets for sensitive files during build
RUN --mount=type=secret,id=application-prod.properties \
    cp /run/secrets/application-prod.properties /app/src/main/resources/application-prod.properties

# Build the backend application
RUN mvn clean package -DskipTests

# Stage 2: Build the React frontend
FROM node:14 AS frontend-build

# Set the working directory for the frontend
WORKDIR /app/webapp

# Copy the frontend source code
COPY webapp/ .

# Use secrets for sensitive files during build
RUN --mount=type=secret,id=prod_env_react \
    cp /run/secrets/prod_env_react /app/webapp/src/.env.prod

# Install frontend dependencies and build for production
RUN npm install
RUN npm run build:prod

# Stage 3: Create the final image
FROM amazoncorretto:17

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the backend build stage to the final image
COPY --from=build /app/target/*.jar app.jar

# Copy the built frontend static files to the Spring Boot resources directory
COPY --from=frontend-build /app/webapp/build/ /app/src/main/resources/static/

# Expose backend server port
EXPOSE 8080

# Run the Spring Boot application with the built frontend
CMD ["java", "-jar", "app.jar"]