EXPOSE 8080
# Use the official OpenJDK base image
FROM openjdk:11-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build context into the container
COPY build/libs/greenNare-0.0.1-SNAPSHOT.jar greenNare-0.0.1-SNAPSHOT.jar

# Specify the command to run when the container starts
CMD ["java", "-jar", "greenNare-0.0.1-SNAPSHOT.jar"]