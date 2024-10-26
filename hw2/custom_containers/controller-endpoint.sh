#!/bin/bash

# Environments
# 
# NODE_ID=1
# QUORUM_VOTERS=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093
# LOG_DIRS=log.dirs=/opt/kafka/data/
# KAFKA_CLUSTER_ID

cat /opt/kafka/config/kraft/controller.properties.template | envsubst > /opt/kafka/config/kraft/controller.properties

/opt/kafka/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c /opt/kafka/config/kraft/controller.properties


