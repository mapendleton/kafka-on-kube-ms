FROM openjdk:11-jre-slim-buster
COPY build/libs/*.jar restservice-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","restservice-0.0.1-SNAPSHOT.jar"]