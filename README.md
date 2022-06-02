# Kafka-on-kube-ms
WIP - Restful app for connecting to kafka through endpoints.

Included now : 
    - Producer
## Setup
1. Clone repo
2. open src/main/resources/application.yml
    - modify spring.kafka.bootstrap-servers with the exposed server:port of your Kafka cluster
    ```yaml
    spring:
        kafka:
            bootstrap-servers: localhost:30080,172.17.0.5:9092
    ```
    - The servers listed are examples of : 
        - an external connection (app outside of kube cluster that kafka is on, connecting to 30080, an exposed nodeport. but we connect to localhost:30080 because that's a port exposed my minikube)
        - an internal connection (app deployed in same kubernetes cluster as Kafka, the endpoint 172.17.0.5:9092 is exposed by an internal kube service) 
Open a shell in root of the repo, then run 
```bash
./gradlew build #this will run the unit tests as well
```
## Run
```bash
docker build -t <imageName> .
```
Now you can either run the program with : 
```bash
#as a container
docker run -p 8080:8080 <imageName>
```
or
```bash
./gradlew bootRun
```
## Tests
```bash
./gradlew test #tests do not rely on an active Kafka cluster
```
# REST API
Use another terminal and run curl requests at http://localhost:8080
## Post to Topic
Currently not set up to create topic if it doesn't exist, topic needs to exist in Kafka cluster the app connects to
#### Request
'POST /topics/{topic}'
```bash
curl -i -X POST localhost:8080/topics/my-topic -H 'Content-Type:application/hal+json' -d '{"id":1,"content":"hello"}'
```
#### Response
```bash
HTTP/1.1 201 
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Wed, 01 Jun 2022 16:46:23 GMT

{"id":1,"content":"!!!!"}
```
## Todo

- [] Add consumer
- [] Add logging
- [] build deployment process
- [] create build/test pipeline
- [] Add GET for listing topics
- [] Add hateos links