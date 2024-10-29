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





# настройки SASL/PLAIN
* listeners=SASL_PLAINTEXT://:9092
* advertised.listeners=SASL_PLAINTEXT://broker11:9092
* inter.broker.listener.name=SASL_PLAINTEXT
* security.protocol=SASL_PLAINTEXT                                         
* sasl.enabled.mechanisms=PLAIN

# авторизация
* authorizer.class.name=org.apache.kafka.metadata.authorizer.StandardAuthorizer # kraft


# данные по пользователям вынесены в файл
- kafka_server_jaas.conf


