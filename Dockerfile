FROM openjdk:11-jre-slim-buster
COPY build/libs/*.jar ./
ENTRYPOINT ["java","-jar","./restservice-0.0.1-SNAPSHOT.jar"]