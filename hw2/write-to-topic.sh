#!/bin/bash


#echo "test 1" | KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker11:9092 --producer.config client_plaintext.properties
#echo "test 2" | KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker12:9092 --producer.config client_plaintext.properties
#echo "test 3" | KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties

echo "test 3" | KAFKA_OPTS="-Djava.security.auth.login.config=./wrong-jass.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties

echo "write success"
