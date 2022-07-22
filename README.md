# Kafka-on-kube-ms
WIP - Restful app for connecting to kafka through endpoints.

Included now : 
    - Producer
    - KafkaListener (consumer)

In order to run locally, you will need:
MAC OS: [colima] (https://github.com/abiosoft/colima), [minikube] (https://minikube.sigs.k8s.io/docs/start/)  
WIN: Docker Desktop for Windows, WSL2
  

## Setup
These are the steps to run kafka and the service in kubernetes, locally.  
1. Clone repo (For windows, clone the repo into WSL Distro, as recommended in https://docs.docker.com/desktop/windows/wsl/)
2. Also run ``` git submodule foreach git pull ``` to update submodules (currently only one: bash-libs)
3. Run ``` ./local-setup/start.sh```
    - If you want to run the app locally you can also add -l or --local options like ```./local-setup/start.sh -l```

You should now be able to hit the available endpoints on localhost. i.e. localhost:8084/health should return "OK"

----
You can also build and run locally:  
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
docker run -p 8084:8084 <imageName>
```
or
```bash
./gradlew bootRun
```
This will use the localhost:30080 port to connect externally to the kafka cluster in kubernetes.
You would still use localhost:8084 to hit the endpoints, the app will utilize the correct port to connect to kafka.
You can also access kafka utilizing the kafka binary scripts by using --bootstrap-server localhost:30080
## Tests
```bash
./gradlew test #tests do not rely on an active Kafka cluster
```
## Consumer (KafkaListener/Web-Socket)
The listener is set up to listen to the default 'my-topic' topic. Meaning whenever a message is produced to that topic, the listener will log the message received and send it to a web-socket endpoint.
/topic/consumer

Apps like https://github.gapinc.com/services-integration/kafka-on-kube-react-app
can subscribe to the web-socket (using stomp and SockJs) to then read those messages
```
session.subscribe("/topic/consumer")
```
**NOTICE**
When running locally with ./gradlew bootRun, you'll notice in the logs when the app starts up, if a topic partition has been assigned for the default topic 'my-topic'. If a partion has not been assigned ie. shows up as '[]' empty. Then the listener will not pick up any messages. 
# REST API
Use another terminal and run curl requests at http://localhost:8084
## Post to Topic
Set up to create topic if it doesn't exist
default topic : my-topic
#### Request
'POST /topics/{topic}'
```bash
curl -iX POST localhost:8084/topics/my-topic -H 'Content-Type:application/hal+json' -d '{"id":1,"content":"hello"}'
```
#### Response
```bash
HTTP/1.1 201 
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Wed, 01 Jun 2022 16:46:23 GMT

{"id":1,"content":"hello"}
```
#### Logs
You should be able to see in the logs (if you run ./gradlew bootRun local you can see it in the console otherwise run kubectl logs <podname> or check minikube dashboard)
When sending: 
```
2022-06-28 05:33:54.765 INFO 1 --- [aListener-0-C-1] c.g.s.r.s.KafkaConsumer : Message consumed on topic: my-topic,Key: 1, Message: hello
```
The Listender should pick up the message as well and return : 
```
2022-06-28 05:33:54.727 INFO 1 --- [nio-8084-exec-1] c.g.s.r.s.KafkaProducer : Sending Message: hello
```
If you have the kafka binary scripts you can run the following to verify message is in the topic: 
```
./kafka-console-consumer.sh --topic my-topic --from-beginning --bootstrap-server localhost:30080
hello
^CProcessed a total of 1 messages
```
If you don't have the binary scripts, you can exec into the kafka pod in kubernetes and find them there.
You would use localhost:9092 as the --bootstrap-server
## Todo

- [X] Add consumer
- [X] Add logging
- [] build deployment process
- [] create build/test pipeline
- [] Add GET for listing topics
- [] Add hateos links