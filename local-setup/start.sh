#!/usr/bin/env bash

function main() {
    . ./Bash-Libs/Setup-Docker-Minikube-Lib.sh

    getEnv 
    startMinikube
    deployKafka
    build_and_containerize_app
    kubectl port-forward service/kafkarestservice 8084:8084 &
    echo "Done..."
}

function deployKafka () {
    echo "deploying zookeeper..."
    kubectl apply -f ./local-setup/zookeeper.yml
    echo "deploying kafka..."
    kubectl apply -f ./local-setup/kafka.yml
}

function build_and_containerize_app() {
    ./gradlew build
    docker build . -f Dockerfile --tag kafka-ms:0.0.1 
    # run app in kubernetes
    kubectl apply -f deployment.yaml

    waitPrompt "kubectl get service/kafkarestservice" "kafkarestservice service running"
    waitPrompt "kubectl get pod/kafkarestservice-6685897dbf-wh5jd" "kafkarestservice pod running"
}

main
