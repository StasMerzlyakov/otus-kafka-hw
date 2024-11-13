#!/bin/bash

./kafka/bin/kafka-topics.sh --create --topic topic1 --bootstrap-server localhost:9092
./kafka/bin/kafka-topics.sh --create --topic topic2 --bootstrap-server localhost:9092

