#!/bin/bash

docker exec -it kafka bash -c "seq 42 | kafka-console-producer --broker-list localhost:9092 --topic event && echo 'Produced 42 messages.'"

echo "write success"
