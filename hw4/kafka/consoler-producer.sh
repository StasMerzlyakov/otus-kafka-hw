#!/bin/bash

for i in $(seq 20); do
  for key in $(seq 5); do
    echo "send $key $i"
	  docker exec -it kafka bash -c \
		  "printf '{\"key\":\"%s\",\"value\":\"%s\"}\n' \"$key\" \"event_${key}_${i}\" | kafka-console-producer --broker-list localhost:9092 --topic events"
	  sleep 2s
  done
done

echo "write success"

