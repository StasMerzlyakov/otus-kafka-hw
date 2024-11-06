#!/bin/bash


# Создаем топик
echo ">> Создаем топики"
read -p "Press enter to continue"
echo ">> от пользователя admin(superuser)"
KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties

wait $!
echo ">> от пользователя alice"
KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test2 \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties
wait $!
echo ">> при ошибочном логине"
KAFKA_OPTS="-Djava.security.auth.login.config=./wrong-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test3 \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties
wait $!

echo
echo
echo ">> Даем права на запись для Alice и права на чтение для Bob"
read -p "Press enter to continue"

KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
    --add --allow-principal User:alice --operation Write --topic test --command-config client_plaintext.properties
wait $!

KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
    --add --allow-principal User:bob --operation Read --topic test --group readers --command-config client_plaintext.properties
wait $!


echo
echo

echo ">> Работаем от пользователя Alice:"
read -p "Press enter to continue"
echo ">>   получаем список топиков:"

KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-topics.sh --list \
	--bootstrap-server broker11:9092 --command-config client_plaintext.properties
wait $!


echo ">>   пишем в топик test: "
echo "hello from alice" | KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties 
wait $!

echo ">>   читаем из топика test:"
KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-console-consumer.sh \
	--topic test --consumer-property group.id=readers  --timeout-ms 5000 --from-beginning --bootstrap-server broker11:9092 --consumer.config client_plaintext.properties
wait $!

echo
echo
echo ">> Работаем от пользователя Bob:"
read -p "Press enter to continue"
echo ">>   получаем список топиков:"

KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-topics.sh --list \
    --bootstrap-server broker11:9092 --command-config client_plaintext.properties
wait $!


echo ">>   пишем в топик test: "
echo "hello from bob" | KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties
wait $!

echo ">>   читаем из топика test:"
KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-console-consumer.sh --consumer-property group.id=readers --timeout-ms 5000 --topic test --from-beginning --bootstrap-server broker11:9092 --consumer.config client_plaintext.properties
wait $!


echo
echo
echo ">> Работаем от пользователя Tom:"
read -p "Press enter to continue"
echo ">>   получаем список топиков:"

KAFKA_OPTS="-Djava.security.auth.login.config=./tom-jaas.conf" build/kafka/bin/kafka-topics.sh --list \
    --bootstrap-server broker11:9092 --command-config client_plaintext.properties
wait $!


echo ">>   пишем в топик test: "
echo "hello from bob" | KAFKA_OPTS="-Djava.security.auth.login.config=./tom-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties
wait $!

echo ">>   читаем из топика test:"
KAFKA_OPTS="-Djava.security.auth.login.config=./tom-jaas.conf" build/kafka/bin/kafka-console-consumer.sh --consumer-property group.id=readers --timeout-ms 5000 --topic test --from-beginning --bootstrap-server broker11:9092 --consumer.config client_plaintext.properties
wait $!










