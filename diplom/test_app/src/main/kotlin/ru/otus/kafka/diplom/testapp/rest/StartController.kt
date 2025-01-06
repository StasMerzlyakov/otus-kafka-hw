package ru.otus.kafka.diplom.testapp.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.otus.kafka.diplom.testapp.db.DbEventService
import ru.otus.kafka.diplom.testapp.domain.DbEvent
import ru.otus.kafka.diplom.testapp.domain.EventType
import ru.otus.kafka.diplom.testapp.domain.KafkaEvent
import ru.otus.kafka.diplom.testapp.domain.ResultCode
import ru.otus.kafka.diplom.testapp.kafka.KafkaEventService
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

@RestController
class StartController {
    private val doEvents = AtomicBoolean(false)

    private val logger = LoggerFactory.getLogger(StartController::class.java)

    @Autowired
    private lateinit var dbEventService: DbEventService

    @Autowired
    private lateinit var kafkaEventService: KafkaEventService

    private val entryPointList = listOf(
        "browsedrive.gov/accept",
        "skiptube.gov",
        "vitz.mil:8074",
        "teklist.net/do/hello",
        "linktype.com:12345",
        "cogilith.info/receive",
    )

    private var job: Job? = null

    @Operation(summary = "start process")
    @RequestMapping(method = [RequestMethod.POST], value = ["/start"])
    suspend fun start(
        @Parameter(
            name = "delay",
            description = "delay between processes",
            required = false,
            example = "1000",
        )
        @RequestParam("delay", defaultValue = "1000")
        delayMls: Long,
    ) {
        logger.info("start events creation invoked")
        if (doEvents.compareAndSet(false, true)) {
            logger.info("starting process")
            job = GlobalScope.launch {
                while (doEvents.get()) {
                    async {
                        val processId = UUID.randomUUID()

                        kafkaEventService.addEvent(
                            KafkaEvent(
                                processId = processId,
                                eventTime = OffsetDateTime.now(),
                                endpoint = entryPointList[Random.nextInt(entryPointList.size)],
                                eventType = EventType.Event1000,
                            ),
                        )

                        // задержка от 500 млс + до 1500 млс
                        delay(500 + Random.nextLong(1000))

                        val resultCode = when (Random.nextInt(10)) {
                            0 -> ResultCode.BAD_REQUEST
                            1 -> ResultCode.INTERNAL_SERVER_ERROR
                            else -> ResultCode.OK
                        }
                        dbEventService.addEvent(
                            DbEvent(
                                processId = processId,
                                eventTime = OffsetDateTime.now(),
                                resultCode = resultCode,
                            ),
                        )
                    }
                    delay(delayMls)
                }
            }

            logger.info("events creation started")
        } else {
            logger.info("events creation already in process")
        }
    }

    @Operation(summary = "stop process")
    @RequestMapping(method = [RequestMethod.POST], value = ["/stop"])
    suspend fun stop() {
        logger.info("stop events creation invoked")
        doEvents.set(false)
        job?.join()
        job = null
    }
}
