package ru.otus.kafka.diplom.testapp.kafka

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Controller
import ru.otus.kafka.diplom.testapp.domain.AppEvent

@Controller
class AppEventService(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    @Value("\${app_event.topic}")
    private lateinit var topic: String

    private val logger = LoggerFactory.getLogger(AppEventService::class.java)

    @Autowired
    private lateinit var template: KafkaTemplate<String?, AppEvent>

    suspend fun addEvent(appEvent: AppEvent) = withContext(ioDispatcher) {
        template.send(topic, appEvent).completable().thenAccept {
            logger.info("send $appEvent success")
        }
        return@withContext
    }
}
