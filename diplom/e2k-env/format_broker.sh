#!/bin/bash

KAFKA_CLUSTER_ID="$(./broker/bin/kafka-storage.sh random-uuid)"

./broker/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c ./broker/config/kraft/server.properties
