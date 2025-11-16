FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/ChatbotAlge-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "/app.jar"]