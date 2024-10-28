## Настройки kraft:

Делаю на базе kafka/config/kraft/controller.properties

настраиваю
* node.id=1                                                                # 1,2,3 - уникальный для каждого узла
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* offsets.topic.replication.factor=3                                       # кол-во реплик для офсетов


## Настройки broker

Делю на базе kafka/config/kraft/broker.properties

настраиваю

* node.id=11                                                                # 11,12,13 - уникальный для каждого узла; начала с 11 чтоб не пересекаться с node.id для серверов контроллеров
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами
* advertised.listeners=PLAINTEXT://:9092                                   # по-умолчанию в файле listeners=PLAINTEXT://localhost:9092


## Создание образов
- ./build_images.sh


## Запуск
- ./run_cluster.sh



