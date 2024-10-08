#!/bin/bash


docker network inspect kafka-hw1-net >/dev/null 2>&1 || \
    docker network create --driver bridge  kafka-hw1-net

docker run --rm --network=kafka-hw1-net -d -p 2181:2181 --name=zookeeper --hostname=zookeeper otus/zookeeper:v1

docker run --rm --network=kafka-hw1-net -d -p 9092:9092 --name=kafka --hostname=kafka otus/kafka:v1
#  docker run  --rm -it --network=kafka-hw1-net -d -p 19092:9092 --name=kafka --entrypoint /bin/bash  otus/kafka:v1
#  docker exec -it kafka bash
