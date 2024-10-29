#!/bin/bash

docker network inspect kafka-net >/dev/null 2>&1 || \
    docker network create --driver bridge  kafka-net

export KAFKA_CLUSTER_ID="$(build/kafka/bin/kafka-storage.sh random-uuid)"

docker-compose up

docker-compose rm -fsv


