#!/bin/bash

broker/bin/kafka-topics.sh --create --topic events_topic --partitions 4 --bootstrap-server broker:9092



# select pg_drop_replication_slot('debezium'); при необходимости
# 

curl -X POST --data-binary "@kafka-connect/event-connector.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq
curl -X POST --data-binary "@kafka-connect/result-sink.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq


# connect - проверка
#curl http://connect:8083 | jq
#curl http://connect:8083/connector-plugins | jq # должны быть io.debezium.connector.postgresql.PostgresConnector и io.confluent.connect.jdbc.JdbcSinkConnector

