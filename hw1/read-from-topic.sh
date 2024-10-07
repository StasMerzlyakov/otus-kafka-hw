#!/bin/bash

# list topics
#kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 # --describe


# --group 1
kafka/bin/kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server localhost:9092

