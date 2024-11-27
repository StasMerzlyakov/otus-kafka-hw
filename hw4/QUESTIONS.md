Вопросы:

- При перезапуске приложениея появляется исключение:
```org.apache.kafka.streams.errors.MissingSourceTopicException: One or more source topics were missing during rebalance
	at org.apache.kafka.streams.processor.internals.StreamsRebalanceListener.onPartitionsAssigned(StreamsRebalanceListener.java:58)
	at org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.invokePartitionsAssigned(ConsumerCoordinator.java:322)
	at org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.onJoinComplete(ConsumerCoordinator.java:471)
	at org.apache.kafka.clients.consumer.internals.AbstractCoordinator.joinGroupIfNeeded(AbstractCoordinator.java:474)
	at org.apache.kafka.clients.consumer.internals.AbstractCoordinator.ensureActiveGroup(AbstractCoordinator.java:385)
	at org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.poll(ConsumerCoordinator.java:557)
```

