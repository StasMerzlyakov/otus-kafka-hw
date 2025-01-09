#!/bin/bash

# create topic
build/kafka/bin/kafka-topics.sh --create --topic events_topic --partitions 4 --bootstrap-server broker:9092

# create result table
PGPASSWORD=pgpass psql -h postgres -U postgres connect <  result_table.sql

# kafka-connect

# connect - проверка
#curl http://connect:8083 | jq
#curl http://connect:8083/connector-plugins | jq # должны быть io.debezium.connector.postgresql.PostgresConnector и io.confluent.connect.jdbc.JdbcSinkConnector

#curl -X POST --data-binary "@event-connector.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq
# curl -X DELETE http://connect:8083/connectors/event-connector
# curl -X PUT --data-binary "@event-connector.json" -H "Content-Type: application/json" http://connect:8083/connector-plugins/source/config/validate | jq

curl -X POST --data-binary "@kafka-connect/event-connector.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq

curl -X POST --data-binary "@kafka-connect/result-sink.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq

