#!/bin/bash

mkdir -p build

pushd build

# download and unpack
wget -N https://archive.apache.org/dist/kafka/3.6.2/kafka_2.13-3.6.2.tgz
if [[ ! -d "./kafka" ]]; then
	tar xvfz kafka_2.13-3.6.2.tgz && mv ./kafka_2.13-3.6.2 ./kafka
fi
popd

docker build -t otus/broker:v1 -f Dockerfile.broker .
docker build -t otus/connect:v1 -f Dockerfile.connect .

