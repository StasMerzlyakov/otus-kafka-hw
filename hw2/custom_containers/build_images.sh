#!/bin/bash

mkdir -p build

pushd build

# download and unpack
wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
if [[ ! -d "./kafka" ]]; then
	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
fi
popd

docker build -t hw2/kafka-controller:v1 -f Dockerfile.controller .
docker build -t hw2/kafka-broker:v1 -f Dockerfile.broker .

