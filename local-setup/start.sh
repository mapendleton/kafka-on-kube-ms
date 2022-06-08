#!/bin/sh

container_name="minikube";


if [[ "$OSTYPE" == "darwin"* ]]; then
    # mac stuff
    echo "local startup assumed for mac..."
    # check if docker is running
    if ! docker stats --no-stream; then

      # echo "This script uses docker, and it isn't running - please start docker and try again!"
      # exit 1
      echo "docker is not started, starting via colima";
      colima start
      sleep 10

      stopper=0
      while (! docker stats --no-stream && stopper -lt 5); do
        # Docker takes a few seconds to initialize
        echo "Waiting for Docker to launch..."
        sleep 2
        ((stopper+=1))
        echo "stopper $stopper"
      echo "docker daemon is open for the local machine"
      done
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
    sleep 60
    kubectl apply -f ./local-setup/kafka.yml
    sleep 60

    # build and containerize app

    ./gradlew build
    docker build . -f Dockerfile --tag kafka-ms:0.0.1
    sleep 60
    # run app in kubernetes
    kubectl apply -f deployment.yaml

    # forward port to localhost
    sleep 60
    kubectl port-forward service/kafkarestservice 8084:8084 &
else
  echo "setup steps not performed...or not configured for OS..."
  exit 1
fi


