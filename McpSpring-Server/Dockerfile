FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY target/McpSpring-Server-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8899
ENTRYPOINT ["java", "-jar", "/app.jar"]