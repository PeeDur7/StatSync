# Use an official lightweight Java 17 image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy everything from your backend into the container
COPY . .

# Second run
RUN chmod +x ./mvnw

# Build the app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Run the JAR file (update the name if your JAR file is different)
CMD ["java", "-jar", "target/NFL-NBAAnalytics-0.0.1-SNAPSHOT.jar"]
