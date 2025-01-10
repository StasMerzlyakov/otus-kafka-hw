#!/bin/bash

# Environments
# 
# KAFKA_BROKERCONNECT

cat /opt/kafka/config/connect-distributed.properties.template | envsubst > /opt/kafka/config/connect-distributed.properties

/opt/kafka/bin/connect-distributed.sh /opt/kafka/config/connect-distributed.properties


