#!/bin/bash

# Environments
# 
# NODE_ID=1
# QUORUM_VOTERS=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093
# LOG_DIRS=log.dirs=/opt/kafka/data/
# KAFKA_CLUSTER_ID

if [ -z "$KAFKA_CLUSTER_ID" ]; then
  echo "Container failed to start, pls pass -e KAFKA_CLUSTER_ID=uuid"
  exit 1
fi

cat /opt/kafka/config/kraft/controller.properties.template | envsubst > /opt/kafka/config/kraft/controller.properties

cat /opt/kafka/config/kraft/kafka_controller_jaas.conf.template | envsubst > /opt/kafka/config/kraft/kafka_controller_jaas.conf

/opt/kafka/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c /opt/kafka/config/kraft/controller.properties

KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/config/kraft/kafka_controller_jaas.conf" /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/controller.properties
