#!/bin/bash

# download and unpack
wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
if [[ ! -d "./kafka" ]]; then
	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
fi

# build images
docker build -t otus/zookeeper:v1 -f docker/Dockerfile.zookeeper .
docker build -t otus/kafka:v1 -f docker/Dockerfile.kafka .
