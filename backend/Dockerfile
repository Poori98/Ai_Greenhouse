# Use OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Grant execute permission to Maven wrapper
RUN chmod +x mvnw

# Expose the application port
EXPOSE 8080

# Run the application with custom JVM args
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-Dcom.mongodb.dns.resolver=dnsjava"]
