#!/bin/bash

../environment/build/kafka/bin/kafka-consumer-groups.sh --bootstrap-server broker:9092 \
  --group group1 --topic events_topic --reset-offsets --to-earliest