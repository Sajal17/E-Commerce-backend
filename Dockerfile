# Step 1: Use lightweight JDK image
FROM eclipse-temurin:21-jdk-alpine


# Step 2: Set working directory inside the container
WORKDIR /app

# Step 3: Copy your JAR file (after you build it)
COPY target/M-Mart-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port your app runs on
EXPOSE 8080

# Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
