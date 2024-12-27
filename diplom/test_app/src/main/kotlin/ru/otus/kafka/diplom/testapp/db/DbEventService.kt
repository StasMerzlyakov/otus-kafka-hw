package ru.otus.kafka.diplom.testapp.db

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import ru.otus.kafka.diplom.testapp.domain.DbEvent

@Controller
class DbEventService {

    private val logger = LoggerFactory.getLogger(DbEventService::class.java)

    @Autowired
    private lateinit var repository: DbEventRecordRepository

    @Transactional
    suspend fun addEvent(dbEvent: DbEvent) {
        repository.save(DbEventRecord.fromDbEvent(dbEvent))
        logger.info("send $dbEvent success")
    }
}