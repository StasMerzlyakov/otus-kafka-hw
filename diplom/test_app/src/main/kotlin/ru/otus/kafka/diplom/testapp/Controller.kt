package ru.otus.kafka.diplom.testapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.otus.kafka.diplom.testapp.db.DbEventService
import ru.otus.kafka.diplom.testapp.domain.AppEvent
import ru.otus.kafka.diplom.testapp.domain.DbEvent
import ru.otus.kafka.diplom.testapp.domain.ResultCode
import ru.otus.kafka.diplom.testapp.kafka.AppEventService
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

@RestController
class Controller(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val doEvents = AtomicBoolean(false)
    private val isRunning = AtomicBoolean(false)

    private val logger = LoggerFactory.getLogger(Controller::class.java)

    @Autowired
    private lateinit var dbEventService: DbEventService

    @Autowired
    private lateinit var appEventService: AppEventService

    @PostMapping("/start")
    suspend fun start(
        @RequestParam("delay", defaultValue = "1000")
        delayMls: Long
    ) {
        if (isRunning.compareAndSet(false, true)) {

            GlobalScope.launch(start = CoroutineStart.LAZY) {
                while (isRunning.get()) {
                    launch(start = CoroutineStart.LAZY) {
                        val processId = UUID.randomUUID()
                        appEventService.addEvent(AppEvent(OffsetDateTime.now(), processId))

                        // задержка от 500 млс + до 1500 млс
                        delay(500 + Random.nextLong(1000))

                        val resultCode = when (Random.nextInt(10)) {
                            0 -> ResultCode.BAD_REQUEST
                            1 -> ResultCode.INTERNAL_SERVER_ERROR
                            else -> ResultCode.OK
                        }
                        dbEventService.addEvent(DbEvent(OffsetDateTime.now(), processId, resultCode))
                    }
                    delay(delayMls)
                }
            }
            logger.info("events creation started")
        } else {
            logger.info("events creation already in process")
        }

    }

    @PostMapping("/start")
    suspend fun stop() {
        doEvents.set(false)
    }

}