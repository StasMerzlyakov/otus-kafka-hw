package ru.otus.kafka.diplom.testapp.kafka

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Controller
import ru.otus.kafka.diplom.testapp.domain.KafkaEvent
import java.util.UUID

@Controller
class KafkaEventService(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    @Value("\${kafka_event.topic}")
    private lateinit var topic: String

    private val logger = LoggerFactory.getLogger(KafkaEventService::class.java)

    @Autowired
    private lateinit var template: KafkaTemplate<UUID, KafkaEvent>

    suspend fun addEvent(kafkaEvent: KafkaEvent) = withContext(ioDispatcher) {
        template.send(topic, kafkaEvent.processId, kafkaEvent).completable().thenAccept {
            logger.info("send $kafkaEvent success")
        }
        return@withContext
    }
}
