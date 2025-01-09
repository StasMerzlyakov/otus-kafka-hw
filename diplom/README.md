# Прототип системы анализа данных на связке Kafka, Kafka-Connect, Akka? (Spark).
## Некоторые особенности использования Kafka на Elbrus 8CB.


## Описание модулей

### kafka-connect
#### Запись событий из БД в kafka (debezium)

```
key.converter=org.apache.kafka.connect.storage.StringConverter

key.converter.schemas.enable=false
value.converter.schemas.enable=false
```
##### настройки event-connector
Добился того, что в топиках kafka_events и db_events данные выглядят одинаково и имеют один ключ - 
process_id(важно для распределения по партициям). 

Трансформации ("transforms": "unwrap,changeTopicName,createKey,extractProcessId,insertTypeIdHeader,replaceField"):
- unwrap (io.debezium.transforms.ExtractNewRecordState) - преобразование формата debezium в json со схемой
- changeTopicName (org.apache.kafka.connect.transforms.RegexRouter) позволяет изменить имя топика (можно написать регулярку от названия таблицы)
- createKey (org.apache.kafka.connect.transforms.ValueToKey) - извлекаем из json только (в виде {process_id:"uuid"})
- extractProcessId (org.apache.kafka.connect.transforms.ExtractField$Key) оставляю только uuid 
- insertTypeIdHeader(org.apache.kafka.connect.transforms.InsertHeader) - доп.поле
- replaceField (org.apache.kafka.connect.transforms.ReplaceField$Valu) - удаляю лишнее
- insertEventType (org.apache.kafka.connect.transforms.InsertField$Value) - добавляю поле event_type


```bash
pushd environment
./init_cluster.sh
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

### akka
Приложение на akka-streams, обрабатывающее данные из топика events_topic
- группирует данные в окна по 30s, со смещением в 10s
- вычисляет для каждого endpoint tps (30s / кол-во ответов)
- вычисляет для каждого endopint avg (среднее время на выполнение одного запроса)
- значение result_code пока игнорируется (TODO)
- результаты приложение пишет в топик speed_topic

```bash
sbt assebly
java -jar target/scala-2.13/akka-assembly-0.1.jar
```


### kafka-connect (JdbcSink)
Отвечает за перенос данных из топика speed_topic в таблицу PostgreSQL speed_result


## TODO (на будущее)
- akka clustering (с учетом распределения данных по партициям)
- kafka stream clustering (партициирование + отказоустойчивость)
- вместо akka попробовать аналитическую БД

- ## Материалы
- [Kafka Connect Deep Dive – Converters and Serialization Explained](https://www.confluent.io/blog/kafka-connect-deep-dive-converters-serialization-explained/#json-schemas)
- [Windowing using Akka Streams and Scala](https://dvirgiln.github.io/akka-streams-windowing/)
- [Windowing data in Akka Streams](https://softwaremill.com/windowing-data-in-akka-streams/)
- [Как синхронизировать сотни таблиц базы в Kafka, не написав ни одного продюсера](https://habr.com/ru/companies/deliveryclub/articles/529484/#9)
- [https://www.confluent.io/hub/confluentinc/kafka-connect-json-schema-converter](https://www.confluent.io/hub/confluentinc/kafka-connect-json-schema-converter)

