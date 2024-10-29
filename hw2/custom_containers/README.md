# Настройка кластера KRaft
## Настройки kraft:

Делаю на базе kafka/config/kraft/controller.properties

настраиваю controller
* node.id=1                                                                # 1,2,3 - уникальный для каждого узла
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* offsets.topic.replication.factor=3                                       # кол-во реплик для офсетов


## Настройки broker

Делю на базе kafka/config/kraft/broker.properties

настраиваю broker

# настройки кластер
* node.id=11                                                               # 11,12,13 - уникальный для каждого узла; начала с 11 чтоб не пересекаться с node.id для серверов контроллеров
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* listeners=PLAINTEXT://:9092                                              # по-умолчанию в файле listeners=PLAINTEXT://localhost:9092
* advertised.listeners=PLAINTEXT://broker11:9092                           #




# Настройка SASL
# настраиваю только содинение client->broker в broker.properties.template (controller.properties не трогаю)


# настройки SASL/PLAIN
* listeners=CLIENT://:9092,BROKER://:9093                                  # для 9092 - для клиентский подключений, 9093 для межсерверных
* advertised.listeners=CLIENT://broker${NODE_ID}:9092,BROKER://broker${NODE_ID}:9093

# secity.protocol.map - пробовал и  BROKER SASL_PLAINTEXT и BROKER:PLAINTEXT
* listener.security.protocol.map=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL,BROKER:SASL_PLAINTEXT,CLIENT:SASL_PLAINTEXT # BROKER SASL_PLAINTEXT, CLINET - SASL_PLAINTEXT

* sasl.enabled.mechanisms=PLAIN
* sasl.mechanism.inter.broker.protocol=PLAIN


# авторизация
* authorizer.class.name=org.apache.kafka.metadata.authorizer.StandardAuthorizer
* super.users=User:admin;
* allow.everyone.if.no.acl.found=false


# данные по пользователям вынесены в файл
* kafka_broker_jaas.conf

# запускаю:
* KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/private/kafka_broker_jaas.conf" /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/broker.properties


# Ход работ
- ./build_images.sh       # собираю образы
- ./run_containers.sh     # запускаю кластер
- ./test.sh               # делаю запросы; 

# Результаты на текущий момент:
- работает аутентификация
- при вызое acl-команд вылетает:
```bash
Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=test, patternType=LITERAL)`: 
 	(principal=User:alice, host=*, operation=WRITE, permissionType=ALLOW) 

Error while executing ACL command: org.apache.kafka.common.errors.SecurityDisabledException: No Authorizer is configured.
java.util.concurrent.ExecutionException: org.apache.kafka.common.errors.SecurityDisabledException: No Authorizer is configured.
	at java.base/java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:395)
	at java.base/java.util.concurrent.CompletableFuture.get(CompletableFuture.java:2005)
	at org.apache.kafka.common.internals.KafkaFutureImpl.get(KafkaFutureImpl.java:165)
	at kafka.admin.AclCommand$AdminClientService.$anonfun$addAcls$3(AclCommand.scala:116)
	at scala.collection.TraversableLike$WithFilter.$anonfun$foreach$1(TraversableLike.scala:985)
	at scala.collection.immutable.Map$Map1.foreach(Map.scala:193)
	at scala.collection.TraversableLike$WithFilter.foreach(TraversableLike.scala:984)
	at kafka.admin.AclCommand$AdminClientService.$anonfun$addAcls$1(AclCommand.scala:113)
	at kafka.admin.AclCommand$AdminClientService.addAcls(AclCommand.scala:112)
	at kafka.admin.AclCommand$.main(AclCommand.scala:74)
	at kafka.admin.AclCommand.main(AclCommand.scala)
Caused by: org.apache.kafka.common.errors.SecurityDisabledException: No Authorizer is configured.
```




