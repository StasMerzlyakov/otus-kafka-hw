package ru.otus.kafka.diplom.testapp.db

import ru.otus.kafka.diplom.testapp.domain.DbEvent
import java.time.OffsetDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class DbEventRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val eventTime: OffsetDateTime,

    val processId: UUID,

    val resultCode: String,
) {
    companion object {
        fun fromDbEvent(dbEvent: DbEvent): DbEventRecord {
            return DbEventRecord(
                eventTime = dbEvent.eventTime,
                processId = dbEvent.processId,
                resultCode = dbEvent.resultCode.code,
            )
        }
    }
}
