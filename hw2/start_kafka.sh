#!/bin/bash

# download and unpack
wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
if [[ ! -d "./kafka" ]]; then
	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
fi

# build images
docker build -t otus/zookeeper:v1 -f Dockerfile.zookeeper .
docker build -t otus/kafka:v1 -f Dockerfile.kafka .


docker network create -d bridge kafka-net

docker run --rm --network=kafka-net -d -p 2181:2181 -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 --name=zookeeper otus/zookeeper:v1

docker run --rm --network=kafka-net -d -p 9092:9092 \
	-e KAFKA_BROKER_ID=1 \
	-e KAFKA_ZOOKEEPER_CONNECT=${DOCKER_HOST_IP:-127.0.0.1}:2181 \
	-e KAFKA_ADVERTISED_LISTENERS=LISTENER_DOCKER_EXTERNAL://${DOCKER_HOST_IP:-127.0.0.1}:9092 \
	-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=LISTENER_DOCKER_EXTERNAL:PLAINTEXT \
	-e KAFKA_LOG4J_ROOT_LOGLEVEL=INFO \
	-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
	-e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 \
	-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
	-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
	--name=kafka \
	otus/kafka:v1

