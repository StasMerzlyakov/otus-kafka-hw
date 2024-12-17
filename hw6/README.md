# Сборка
```bash
./build-images.sh
```

# запуск
```bash
./start-compose.sh
```

# проверка статусов коннектров
```bash
curl http://connect:8083 | jq
curl http://connect:8083/connector-plugins | jq
```

# создаю табличку в postgres
```bash
docker exec -ti postgres psql -U kafka connect
CREATE TABLE customers (id INT PRIMARY KEY, name TEXT, age INT);
INSERT INTO customers (id, name, age) VALUES (1, 'Fred', 34);
\q
```

# создаю коннектор
```
curl -X POST --data-binary "@customers.json" -H "Content-Type: application/json" http://connect:8083/connectors | jq
```


# запускаем чтение топика (в отдельной консоли)
```bash
docker exec kafka kafka-console-consumer --topic postgres.public.customers --bootstrap-server kafka:9092 --property print.offset=true --property print.key=true --from-beginning
```


# создаю разные запросы к customers
```bash

user@stsm:~/Study/Otus/otus-kafka-hw/hw6$ docker exec -ti postgres psql -U kafka connect
psql (16.4 (Debian 16.4-1.pgdg120+2))
Type "help" for help.

connect=# 
connect=# INSERT INTO customers (id, name, age) VALUES (2, 'Fred', 34);
INSERT 0 1
connect=# UPDATE customers set name='John' where id = 2;
UPDATE 1
connect=# select * from customers;
 id | name | age 
----+------+-----
  1 | Fred |  34
  2 | John |  34
(2 rows)

```

# вижу в консоли с топиком вывод
```
user@stsm:~/Work/Voskhod/NEW_GENERATION/services/fxmlDsigner/tests$ docker exec kafka /opt/kafka/bin/kafka-console-consumer.sh --topic postgres.public.customers --bootstrap-server kafka:9092 --property print.offset=true --property print.key=true --from-beginning
Offset:0	Struct{id=1}	{"before.id":null,"before.name":null,"before.age":null,"after.id":1,"after.name":"Fred","after.age":34,"source.version":"2.2.1.Final","source.connector":"postgresql","source.name":"postgres","source.ts_ms":1734093814353,"source.snapshot":"last","source.db":"connect","source.sequence":"[null,\"26749744\"]","source.schema":"public","source.table":"customers","source.txId":746,"source.lsn":26749744,"source.xmin":null,"op":"r","ts_ms":1734093814475,"transaction.id":null,"transaction.total_order":null,"transaction.data_collection_order":null}
Offset:1	Struct{id=2}	{"before.id":null,"before.name":null,"before.age":null,"after.id":2,"after.name":"Fred","after.age":34,"source.version":"2.2.1.Final","source.connector":"postgresql","source.name":"postgres","source.ts_ms":1734094353192,"source.snapshot":"false","source.db":"connect","source.sequence":"[null,\"26750072\"]","source.schema":"public","source.table":"customers","source.txId":747,"source.lsn":26750072,"source.xmin":null,"op":"c","ts_ms":1734094353511,"transaction.id":null,"transaction.total_order":null,"transaction.data_collection_order":null}
Offset:2	Struct{id=2}	{"before.id":null,"before.name":null,"before.age":null,"after.id":2,"after.name":"John","after.age":34,"source.version":"2.2.1.Final","source.connector":"postgresql","source.name":"postgres","source.ts_ms":1734094382410,"source.snapshot":"false","source.db":"connect","source.sequence":"[\"26750448\",\"26750504\"]","source.schema":"public","source.table":"customers","source.txId":748,"source.lsn":26750504,"source.xmin":null,"op":"u","ts_ms":1734094382866,"transaction.id":null,"transaction.total_order":null,"transaction.data_collection_order":null}

```

