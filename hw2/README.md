# Разворачиваем кластер Kafka-Kraft:
- 3 контроллера
- 3 брокера


# Настройка кластера KRaft
## Настройки kraft:

Делаю на базе kafka/config/kraft/controller.properties

### controller.properties.template
* node.id=1                                                                # 1,2,3 - уникальный для каждого узла
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* offsets.topic.replication.factor=3                                       # кол-во реплик для офсетов

## Настройки broker

Делю на базе kafka/config/kraft/broker.properties

### broker.properties.template
* node.id=11                                                               # 11,12,13 - уникальный для каждого узла; начала с 11 чтоб не пересекаться с node.id для серверов контроллеров
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* listeners=PLAINTEXT://:9092                                              # по-умолчанию в файле listeners=PLAINTEXT://localhost:9092
* advertised.listeners=PLAINTEXT://broker11:9092                           #


# Настройка SASL
настраиваю все соединения client->broker, broker->broker, broker->controller на SASL_PLAINTEXT


## broker
### broker.properties.template

* listeners=CLIENT://:9092,BROKER://:9093                                  # для 9092 - для клиентский подключений, 9093 для межсерверных
* inter.broker.listener.name=BROKER
* controller.listener.names=CONTROLLER
* advertised.listeners=CLIENT://broker${NODE_ID}:9092,BROKER://broker${NODE_ID}:9093
* listener.security.protocol.map=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL,BROKER:SASL_PLAINTEXT,CLIENT:SASL_PLAINTEXT
* sasl.enabled.mechanisms=PLAIN
* sasl.mechanism.inter.broker.protocol=PLAIN
* sasl.inter.broker.protocol=PLAIN
* sasl.mechanism.controller.protocol=PLAIN

* authorizer.class.name=org.apache.kafka.metadata.authorizer.StandardAuthorizer
* super.users=User:admin;
* allow.everyone.if.no.acl.found=false

### данные по пользователям вынесены в файл
* kafka_broker_jaas.conf

### запускаю:
* KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/private/kafka_broker_jaas.conf" /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/broker.properties


## controller
### controller.properties.template
* listeners=CONTROLLER://:9093
* controller.listener.names=CONTROLLER
* listener.security.protocol.map=CONTROLLER:SASL_PLAINTEXT
* sasl.enabled.mechanisms=PLAIN
* sasl.mechanism.inter.broker.protocol=PLAIN
* sasl.inter.broker.protocol=PLAIN
* sasl.mechanism.controller.protocol=PLAIN
* authorizer.class.name=org.apache.kafka.metadata.authorizer.StandardAuthorizer
* super.users=User:admin
* allow.everyone.if.no.acl.found=false

### данные по пользователям вынесены в файл
* kafka_controller_jaas.conf.template

### запускаю:
* KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/config/kraft/kafka_controller_jaas.conf" /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/controller.properties


# Ход работ
- ./build_images.sh       # собираю образы
- ./run_containers.sh     # запускаю кластер
- ./test.sh               # делаю запросы; 


# Результаты:
* alice видит test, может писать, не может читать
* bob видит test, не может писать, может читать
* tom не видит test, не может писать, не может читать

```bash
>> Создаем топики
Press enter to continue
>> от пользователя admin(superuser)
Created topic test.
>> от пользователя alice
Error while executing topic command : Authorization failed.
[2024-10-30 12:49:44,766] ERROR org.apache.kafka.common.errors.TopicAuthorizationException: Authorization failed.
 (org.apache.kafka.tools.TopicCommand)
>> при ошибочном логине
[2024-10-30 12:49:46,325] ERROR [AdminClient clientId=adminclient-1] Connection to node -1 (broker13/172.24.0.6:9092) failed authentication due to: Authentication failed: Invalid username or password (org.apache.kafka.clients.NetworkClient)
[2024-10-30 12:49:46,326] WARN [AdminClient clientId=adminclient-1] Metadata update failed due to authentication error (org.apache.kafka.clients.admin.internals.AdminMetadataManager)
org.apache.kafka.common.errors.SaslAuthenticationException: Authentication failed: Invalid username or password
Error while executing topic command : Authentication failed: Invalid username or password
[2024-10-30 12:49:46,328] ERROR org.apache.kafka.common.errors.SaslAuthenticationException: Authentication failed: Invalid username or password
 (org.apache.kafka.tools.TopicCommand)


>> Даем права на запись для Alice и права на чтение для Bob
Press enter to continue
Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=test, patternType=LITERAL)`: 
 	(principal=User:alice, host=*, operation=WRITE, permissionType=ALLOW) 

Adding ACLs for resource `ResourcePattern(resourceType=TOPIC, name=test, patternType=LITERAL)`: 
 	(principal=User:bob, host=*, operation=READ, permissionType=ALLOW) 

Adding ACLs for resource `ResourcePattern(resourceType=GROUP, name=readers, patternType=LITERAL)`: 
 	(principal=User:bob, host=*, operation=READ, permissionType=ALLOW) 

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=test, patternType=LITERAL)`: 
 	(principal=User:alice, host=*, operation=WRITE, permissionType=ALLOW)
	(principal=User:bob, host=*, operation=READ, permissionType=ALLOW) 



>> Работаем от пользователя Alice:
Press enter to continue
>>   получаем список топиков:
test
>>   пишем в топик test: 
>>   читаем из топика test:
[2024-10-30 12:50:12,712] ERROR Error processing message, terminating consumer process:  (org.apache.kafka.tools.consumer.ConsoleConsumer)
org.apache.kafka.common.errors.GroupAuthorizationException: Not authorized to access group: readers
Processed a total of 0 messages


>> Работаем от пользователя Bob:
Press enter to continue
>>   получаем список топиков:
test
>>   пишем в топик test: 
[2024-10-30 12:50:16,298] ERROR [Producer clientId=console-producer] Aborting producer batches due to fatal error (org.apache.kafka.clients.producer.internals.Sender)
org.apache.kafka.common.errors.ClusterAuthorizationException: Cluster authorization failed.
[2024-10-30 12:50:16,304] ERROR Error when sending message to topic test with key: null, value: 14 bytes with error: (org.apache.kafka.clients.producer.internals.ErrorLoggingCallback)
org.apache.kafka.common.errors.ClusterAuthorizationException: Cluster authorization failed.
>>   читаем из топика test:
hello from alice
[2024-10-30 12:50:31,410] ERROR Error processing message, terminating consumer process:  (org.apache.kafka.tools.consumer.ConsoleConsumer)
org.apache.kafka.common.errors.TimeoutException
Processed a total of 1 messages


>> Работаем от пользователя Tom:
Press enter to continue
>>   получаем список топиков:

>>   пишем в топик test: 
[2024-10-30 12:50:36,866] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 1 : {test=TOPIC_AUTHORIZATION_FAILED} (org.apache.kafka.clients.NetworkClient)
[2024-10-30 12:50:36,868] ERROR [Producer clientId=console-producer] Topic authorization failed for topics [test] (org.apache.kafka.clients.Metadata)
[2024-10-30 12:50:36,869] ERROR Error when sending message to topic test with key: null, value: 14 bytes with error: (org.apache.kafka.clients.producer.internals.ErrorLoggingCallback)
org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [test]
[2024-10-30 12:50:36,877] ERROR [Producer clientId=console-producer] Error in kafka producer I/O thread while aborting transaction when during closing:  (org.apache.kafka.clients.producer.internals.Sender)
java.lang.IllegalStateException: Transactional method invoked on a non-transactional producer.
	at org.apache.kafka.clients.producer.internals.TransactionManager.ensureTransactional(TransactionManager.java:1019)
	at org.apache.kafka.clients.producer.internals.TransactionManager.handleCachedTransactionRequestResult(TransactionManager.java:1131)
	at org.apache.kafka.clients.producer.internals.TransactionManager.beginAbort(TransactionManager.java:323)
	at org.apache.kafka.clients.producer.internals.Sender.run(Sender.java:276)
	at java.base/java.lang.Thread.run(Thread.java:829)
>>   читаем из топика test:
[2024-10-30 12:50:38,211] WARN [Consumer clientId=console-consumer, groupId=readers] Error while fetching metadata with correlation id 2 : {test=TOPIC_AUTHORIZATION_FAILED} (org.apache.kafka.clients.NetworkClient)
[2024-10-30 12:50:38,212] ERROR [Consumer clientId=console-consumer, groupId=readers] Topic authorization failed for topics [test] (org.apache.kafka.clients.Metadata)
[2024-10-30 12:50:38,212] ERROR Error processing message, terminating consumer process:  (org.apache.kafka.tools.consumer.ConsoleConsumer)
org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [test]
Processed a total of 0 messages
```

