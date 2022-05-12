# java-springboot-rest-template
Template for starting a Restful, Java, springboot application. 
Uses gradle as a build tool and has a simple Dockerfile that just runs the JAR file.
## Setup
Click on "Use this template".
Open a shell in root of your new repo, then run 
```bash
./gradlew build
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
./gradlew test
```
# REST API
Use another terminal and run curl requests at http://localhost:8080
## Get List
#### Request
'GET /greetings'
```bash
curl -i localhost:8080/greetings
```
#### Response
```bash
HTTP/1.1 200 
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 12 May 2022 22:35:38 GMT

{"_embedded":{"greetingList":[{"id":3,"content":"Howdy, World!","_links":{"self":{"href":"http://localhost:8080/greetings/3?name=World"},"greetings":{"href":"http://localhost:8080/greetings?name=World"}}},{"id":2,"content":"Hola, World!","_links":{"self":{"href":"http://localhost:8080/greetings/2?name=World"},"greetings":{"href":"http://localhost:8080/greetings?name=World"}}},{"id":1,"content":"Hello, World!","_links":{"self":{"href":"http://localhost:8080/greetings/1?name=World"},"greetings":{"href":"http://localhost:8080/greetings?name=World"}}}]},"_links":{"self":{"href":"http://localhost:8080/greetings?name=World"}}}
```
## Get single item 
#### Request
'GET /greetings/id'
```bash
curl -i localhost:8080/greetings/1
```
#### Response
```bash
HTTP/1.1 200 
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 12 May 2022 22:41:33 GMT

{"id":1,"content":"Hello, World!","_links":{"self":{"href":"http://localhost:8080/greetings/1?name=World"},"greetings":{"href":"http://localhost:8080/greetings?name=World"}}}
```
## Create new greeting 
#### Request
'POST /greetings/'
```bash
curl -i POST localhost:8080/greetings -H 'Content-Type:application/json' -d '{"id": 4, "content": "Well hello there!"}'
```
#### Response
```bash
HTTP/1.1 201 
Location: http://localhost:8080/greetings/4
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 12 May 2022 22:40:11 GMT

{"id":4,"content":"Well hello there!","_links":{"self":{"href":"http://localhost:8080/greetings/4{?name}","templated":true},"greetings":{"href":"http://localhost:8080/greetings{?name}","templated":true}}}
```
## Patch item 
#### Request
'PATCH /greetings/id/content'
```bash
curl -i -X PATCH localhost:8080/greetings/1/boo%20bop
```
#### Response
```bash
HTTP/1.1 200 
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 12 May 2022 22:43:16 GMT

{"id":1,"content":"boo bop","_links":{"self":{"href":"http://localhost:8080/greetings/1{?name}","templated":true},"greetings":{"href":"http://localhost:8080/greetings{?name}","templated":true}}}
```
## Delete item 
#### Request
'DELETE /greetings/id'
```bash
curl -i -X DELETE localhost:8080/greetings/1
```
#### Response
```bash
HTTP/1.1 204 
Date: Thu, 12 May 2022 22:45:42 GMT
```
## Get missing item 
#### Request
'GET /greetings/id'
```bash
curl -i localhost:8080/greetings/5
```
#### Response
```bash
HTTP/1.1 404 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 May 2022 22:46:20 GMT

{"timestamp":"2022-05-12T22:46:20.169+00:00","status":404,"error":"Not Found","path":"/greetings/5"}
```
