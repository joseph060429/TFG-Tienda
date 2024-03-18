# Use the official JDK 19 image as the base image for the build stage
FROM maven:3.8.7-eclipse-temurin-19 AS pepon
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY ./backend/pom.xml .
COPY ./backend/src /app/src 
# Build the application using Maven
RUN mvn clean package -DskipTests


# Use an official OpenJDK image as the base image
FROM eclipse-temurin:19 as runtime
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=pepon /app/target/backend-0.0.1-SNAPSHOT.jar .
# Exponer Puerto
EXPOSE 8080
# Set the command to run the application
# CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar", "-Dhttp.proxyHost=172.28.0.3", "-Dhttp.proxyPort=80"]

# mvn spring-boot:run -Dhttp.proxyHost=your.proxy.net -Dhttp.proxyPort=8080
