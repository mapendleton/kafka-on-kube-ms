# Kafka-on-kube-ms
WIP - Restful app for connecting to kafka through endpoints.

Included now : 
    - Producer

In order to run locally, you will need:
MAC OS: [colima] (https://github.com/abiosoft/colima), [minikube] (https://minikube.sigs.k8s.io/docs/start/)  
WIN: TBD 
  

## Setup
These are the steps to run kafka and the service in kubernetes, locally.  
1. Clone repo
2. Run ``` ./local-setup/start.sh```  

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