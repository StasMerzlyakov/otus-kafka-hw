# Прототип системы анализа данных на связке Kafka, Kafka-Connect, Akka? (Spark).
## Некоторые особенности использования Kafka на Elbrus 8CB.


## Описание модулей

### kafka-connect

#### Запись событий из БД в kafka (debezium)

##### настройки connect-distributed.properties
[Kafka Connect Deep Dive – Converters and Serialization Explained](https://www.confluent.io/blog/kafka-connect-deep-dive-converters-serialization-explained/#json-schemas)

```
key.converter.schemas.enable=false
value.converter.schemas.enable=false
```

##### настройки event-connector

Трансформации ("transforms": "unwrap,changeTopicName,payload"):
- unwrap (io.debezium.transforms.ExtractNewRecordState) - преобразование формата debezium в json со схемой
- changeTopicName (org.apache.kafka.connect.transforms.RegexRouter) позволяет изменить имя топика (можно написать регулярку от названия таблицы)
- payload - извлекаем из json только поле payload

```
"transforms": "unwrap,changeTopicName,payload",

"transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
"transforms.unwrap.drop.tombstones": "false",
"transforms.unwrap.delete.handling.mode": "rewrite",
"transforms.changeTopicName.type": "org.apache.kafka.connect.transforms.RegexRouter",
"transforms.changeTopicName.regex": "(.*)",
"transforms.changeTopicName.replacement": "db_events",
"transforms.payload.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
"transforms.payload.type": "payload",
```
```bash
pushd kafka-connect

# connect - проверка
curl http://connect:8083 | jq
curl http://connect:8083/connector-plugins | jq # должен быть io.debezium.connector.postgresql.PostgresConnector

curl -X POST --data-binary "@event-connector.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq
# curl -X DELETE http://connect:8083/connectors/event-connector
# curl -X PUT --data-binary "@event-connector.json" -H "Content-Type: application/json" http://connect:8083/connector-plugins/source/config/validate | jq

curl http://connect:8083/connectors/event-connector/status | jq

```


### test_app

Spring-приложение, имитирующее работу какой-то системы из нескольких сервисов.
Запускает процесс записи двух событий в kafka и, через некоторое время в БД.
```bash
pushd test_app
./gradlew bootRun

curl -X POST localhost:8080/start
curl -X POST localhost:8080/stop

# http://localhost:8080/swagger-ui/index.html
```

