package ru.otus.kafka.diplom.testapp.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
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
class StartController {
    private val doEvents = AtomicBoolean(false)
    private val isRunning = AtomicBoolean(false)

    private val logger = LoggerFactory.getLogger(StartController::class.java)

    @Autowired
    private lateinit var dbEventService: DbEventService

    @Autowired
    private lateinit var appEventService: AppEventService

    private var job : Job? = null

    @Operation(summary = "start process")
    @RequestMapping(method = [RequestMethod.POST], value = ["/start"],)
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
        if (isRunning.compareAndSet(false, true)) {
            logger.info("starting process")
            job = GlobalScope.launch {
                while (isRunning.get()) {
                    async {
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

    @Operation(summary = "stop process")
    @RequestMapping(method = [RequestMethod.POST], value = ["/stop"],)
    suspend fun stop() {
        logger.info("stop events creation invoked")
        doEvents.set(false)
        job?.join()
        job = null
    }
}
