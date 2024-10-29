#!/bin/bash

# Создаем образы
#echo ">> Создаем образы"
#read -p "Press enter to continue"
#
#mkdir -p build
#pushd build
## download and unpack
#wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
#if [[ ! -d "./kafka" ]]; then
#	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
#fi
#popd
#docker build -t hw2/kafka-controller:v1 -f Dockerfile.controller .
#docker build -t hw2/kafka-broker:v1 -f Dockerfile.broker .
#
#
# Запускаем кластер
#echo ">> Запускаем кластер"
#read -p "Press enter to continue"
#docker network inspect kafka-net >/dev/null 2>&1 || \
#    docker network create --driver bridge  kafka-net
#
#export KAFKA_CLUSTER_ID="$(build/kafka/bin/kafka-storage.sh random-uuid)"
#docker-compose up -d
#
#
#
# Создаем топик
echo ">> Создаем topic test"
read -p "Press enter to continue"

KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test \
	--bootstrap-server broker11:9092 --command-config client_plaintext.properties 

#build/kafka/bin/kafka-topics.sh --create --topic test --bootstrap-server broker11:9092


#echo ">> Даем права на запись для Alice и права на чтение для Bob"
#read -p "Press enter to continue"
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server localhost:19092 \
#    --add --allow-principal User:alice --operation Write --topic test --command-config client_plaintext.properties
#
#
#echo ">> Останавилваем кластер"
#read -p "Press enter to continue"
##docker-compose rm -fsv

