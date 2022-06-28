#!/bin/sh
. ./bash-libs/Setup-Docker-Minikube-Lib.sh

minikube delete
colima stop
