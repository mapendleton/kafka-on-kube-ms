#!/bin/sh

container_name="minikube";


if [[ "$OSTYPE" == "darwin"* ]]; then
    # mac stuff
    echo "local startup assumed for mac..."
    # check if docker is running
    if ! docker info > /dev/null 2>&1; then

      # echo "This script uses docker, and it isn't running - please start docker and try again!"
      # exit 1
      echo "docker is not started, starting via colima";
      colima start --kubernetes
      sleep 1

      if ! docker stats --no-stream >/dev/null 2>&1; then
        echo "can't start docker via colima..."
        echo "please start docker in order to continue..."
        exit 1
      fi
      echo "docker started..."
    fi

    # check if minikube is running
    if [ $(docker inspect -f '{{.State.Running}}' $container_name) = "true" ]; then
      echo "minikube is up...";
      else
        echo "docker running but minikube not started...";
        minikube start;
    fi

    # run kafka on kubernetes
    kubectl apply -f ./local-setup/zookeeper.yml
    kubectl apply -f ./local-setup/kafka.yml

    # build and containerize app

    ./gradlew build
    docker build . -f Dockerfile --tag kafka-ms:0.0.1
    # run app in kubernetes
    kubectl apply -f deployment.yaml

else
  echo "setup steps not performed...or not configured for OS..."
  exit 1
fi


