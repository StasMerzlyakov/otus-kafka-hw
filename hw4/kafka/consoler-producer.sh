#!/bin/bash

for i in `seq 42`; do
    echo "send $i"	
    docker exec -it kafka bash -c "echo \"$i:msg\" | kafka-console-producer --broker-list localhost:9092 --topic events --property \"parse.key=true\" --property \"key.separator=:\""
done


echo "write success"
