#!/bin/bash


# Создаем топик
echo ">> Создаем topics"
read -p "Press enter to continue"

KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties &> /dev/null

[[ $? -eq 0 ]] && echo "create topic test by admin(super user) success" || exit 1

KAFKA_OPTS="-Djava.security.auth.login.config=./alice-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test2 \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties &> /dev/null

[[ $? -eq 0 ]] && echo "create topic test2 by alice(not super.user) success" || exit 1

KAFKA_OPTS="-Djava.security.auth.login.config=./wrong-jaas.conf" build/kafka/bin/kafka-topics.sh --create --topic test3 \
    --bootstrap-server broker13:9092 --command-config client_plaintext.properties &> /dev/null

[[ $? -ne 0 ]] && echo "create topic test3 by unauthenticated user error" || exit 1

echo ">> Даем права на запись для Alice и права на чтение для Bob"
read -p "Press enter to continue"

KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
    --add --allow-principal User:alice --operation Write --topic test --command-config client_plaintext.properties
#
#KAFKA_OPTS="-Djava.security.auth.login.config=./admin-jaas.conf" build/kafka/bin/kafka-acls.sh --bootstrap-server broker11:9092 \
#    --add --allow-principal User:bob --operation Read --topic test --command-config client_plaintext.properties
#



