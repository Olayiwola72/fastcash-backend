# Stage 1: Maven Build
FROM maven:3.8.1-openjdk-17-slim AS builder

LABEL maintainer="Olayiwola Akinnagbe"

LABEL version="1.0"

LABEL description="Spring Boot Fast Cash"

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .

COPY .env .
COPY .env .env.production

# Copy the rest of the project files
COPY src ./src

# Resolve Maven dependencies
RUN mvn -e -B dependency:resolve

# Optionally copy .env if it exists
# RUN if [ -f /app/.env ]; then cp /app/.env /app/.env; fi

# Install Node.js (for Node.js version 22.x)
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory in the final image
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/money-transfer-0.0.1-SNAPSHOT.jar /app/money-transfer-0.0.1-SNAPSHOT.jar

# Optionally copy .env if it exists
# RUN if [ -f /app/.env ]; then cp /app/.env /app/.env; fi

# Command to run the application
ENTRYPOINT ["java", "-jar", "money-transfer-0.0.1-SNAPSHOT.jar"]