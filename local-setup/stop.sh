#!/bin/sh
. ./Bash-Libs/Setup-Docker-Minikube-Lib.sh

minikube delete
colima stop
