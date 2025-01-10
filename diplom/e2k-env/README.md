# Конфиги для разветрывания

HINT - в конфигах используются dns-имена для доступа к kafka и postgres - broker и postgres

список пакетов для postgres в папке postgres


# Порядок запуска
- настройка доступа и логической репликации в postgres (см папку postgres)
- format_broker.sh # перед первым старом kafka
- broker_run.sh # запуск kafka
- connect_run.sh # запуск connect
- init_cluster.sh # один раз после запуска kafka и connect
- testapp_run.sh # запуск имитатора активности (пришет в kafa и в БД)
- kafdrop_run.sh # запуск kafdrop (опционально)



