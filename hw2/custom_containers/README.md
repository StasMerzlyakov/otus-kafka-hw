## Настройки kraft:

Делаю на базе kafka/config/kraft/controller.properties

* node.id=1                                                                # 1,2,3 - уникальный для каждого узла
* controller.quorum.voters=1@kraft1:9093,2@kraft2:9093,3@kraft3:9093       # кворум
* log.dirs=/opt/kafka/data/                                                # директория с логами






