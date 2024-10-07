#!/bin/bash


echo "test 1" | kafka/bin/kafka-console-producer.sh --topic test  --broker-list localhost:9092
echo "test 2" | kafka/bin/kafka-console-producer.sh --topic test  --broker-list localhost:9092
echo "test 3" | kafka/bin/kafka-console-producer.sh --topic test  --broker-list localhost:9092

echo "write success"
