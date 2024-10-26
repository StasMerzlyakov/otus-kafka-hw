#!/bin/bash

mkdir -p build

pushd build

# download and unpack
# wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
# if [[ ! -d "./kafka" ]]; then
#	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
#fi

# generate kluster uuid
export KAFKA_CLUSTER_ID="$(kafka/bin/kafka-storage.sh random-uuid)"


controllerGen () {

export NODE_ID=$1
export QUORUM_VOTERS=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
export LOG_DIRS=log.dirs=/opt/kafka/data/

cat ../controller.template | envsubst > controller$1.properties

}

controllerGen 1
controllerGen 2
controllerGen 3

popd




