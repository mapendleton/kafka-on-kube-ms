#!/usr/bin/env bash

function main() {
    . ./bash-libs/Setup-Docker-Minikube-Lib.sh

    get_env 
    start_minikube
    deploy_kafka
    if [[ $1 == "--local" || $1 == "-l" ]]; then
        ./gradlew bootRun
    else
        build_and_containerize_app
        kubectl port-forward service/kafkarestservice 8084:8084 &
    fi
    echo "Done..."
}

function deploy_kafka () {
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

    wait_prompt "kubectl get service/kafkarestservice" "kafkarestservice service running"
    wait_prompt "kubectl get pods -l app=kafkarestservice" "kafkarestservice pod running"
}

main $@
