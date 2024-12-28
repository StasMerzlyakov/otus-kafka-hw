# Прототип системы анализа данных на связке Kafka, Kafka-Connect, Akka? (Spark).
## Некоторые особенности использования Kafka на Elbrus 8CB.


## Описание модулей

### kafka-connect

#### Запись событий из БД в kafka (debezium)

##### настройки connect-distributed.properties
[Kafka Connect Deep Dive – Converters and Serialization Explained](https://www.confluent.io/blog/kafka-connect-deep-dive-converters-serialization-explained/#json-schemas)

```
key.converter=org.apache.kafka.connect.storage.StringConverter

key.converter.schemas.enable=false
value.converter.schemas.enable=false
```

##### настройки event-connector

Добился того, что в топиках kafka_events и db_events данные выглядят одинаково.

Трансформации ("transforms": "unwrap,changeTopicName,createKey,extractProcessId,insertTypeIdHeader,replaceField"):
- unwrap (io.debezium.transforms.ExtractNewRecordState) - преобразование формата debezium в json со схемой
- changeTopicName (org.apache.kafka.connect.transforms.RegexRouter) позволяет изменить имя топика (можно написать регулярку от названия таблицы)
- createKey (org.apache.kafka.connect.transforms.ValueToKey) - извлекаем из json только (в виде {process_id:"uuid"})
- extractProcessId (org.apache.kafka.connect.transforms.ExtractField$Key) оставляю только uuid 
- insertTypeIdHeader(org.apache.kafka.connect.transforms.InsertHeader) - доп.поле
- replaceField (org.apache.kafka.connect.transforms.ReplaceField$Valu) - удаляю лишнее



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

