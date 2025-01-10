#!/bin/bash

# Environments
# KAFKA_CLUSTER_ID

if [ -z "$KAFKA_CLUSTER_ID" ]; then
  echo "Container failed to start, pls pass -e KAFKA_CLUSTER_ID=uuid"
  exit 1
fi


cat /opt/kafka/config/kraft/server.properties.template | envsubst > /opt/kafka/config/kraft/server.properties

/opt/kafka/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c /opt/kafka/config/kraft/server.properties

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/server.properties


