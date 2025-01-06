#!/bin/bash

# create topic
build/kafka/bin/kafka-topics.sh --create --topic events_topic --partitions 4 --bootstrap-server broker:9092

# kafka-connect
curl -X POST --data-binary "@kafka-connect/event-connector.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq

