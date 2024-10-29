#!/bin/bash


# Создаем топик
echo ">> Создаем topics"
read -p "Press enter to continue"

#KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test \
#    --bootstrap-server broker13:9092 --command-config client_plaintext.properties
#
#[[ $? -eq 0 ]] && echo ">> Create topic test by admin(super user) success" || exit 1
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test2 \
#    --bootstrap-server broker13:9092 --command-config client_plaintext.properties
#
#[[ $? -ne 0 ]] && echo ">> Create topic test2 by alice(not super.user) error" || exit 1
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./wrong-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test3 \
#    --bootstrap-server broker13:9092 --command-config client_plaintext.properties
#
#[[ $? -ne 0 ]] && echo ">> Create topic test3 by unauthenticated user error" || exit 1
#
#echo ">> Даем права на запись для Alice и права на чтение для Bob"
#read -p "Press enter to continue"
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
#    --add --allow-principal User:alice --operation Write --topic test --command-config client_plaintext.properties
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
#    --add --allow-principal User:bob --operation Read --topic test --command-config client_plaintext.properties


#echo ">> Работаем от пользователя Alice:"
#read -p "Press enter to continue"
#echo ">>   получаем список топиков:"
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-topics.sh --list \
#    --bootstrap-server broker11:9092 --command-config client_plaintext.properties
#[[ $? -eq 0 ]] && echo ">> успех" || exit 1
#
#
#echo ">>   пишем в топик test: "
#echo "hello from alice" | KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties
#[[ $? -eq 0 ]] && echo ">> успех" || exit 1
#
#echo ">>   читаем из топика test:"
#KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server broker11:9092 --consumer.config client_plaintext.properties
#[[ $? -ne 0 ]] && echo ">> ошибка" || exit 1
#
#
echo ">> Работаем от пользователя Bob:"
read -p "Press enter to continue"
echo ">>   получаем список топиков:"

KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-topics.sh --list \
    --bootstrap-server broker11:9092 --command-config client_plaintext.properties
[[ $? -eq 0 ]] && echo ">> успех" || exit 1


echo ">>   пишем в топик test: "
echo "hello from bob" | KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-console-producer.sh --topic test  --broker-list broker13:9092 --producer.config client_plaintext.properties
res=$?


[[ $res -eq 1 ]] && echo ">> ошибка" || exit 1

echo ">>   читаем из топика test:"
KAFKA_OPTS="-Djava.security.auth.login.config=./bob-jaas.conf" build/kafka/bin/kafka-console-consumer.sh --consumer-property group.id=bob --topic test --from-beginning --bootstrap-server broker11:9092 --consumer.config client_plaintext.properties
res=$?

[[ $res -eq 0 ]] && echo ">> успех" || exit 1











