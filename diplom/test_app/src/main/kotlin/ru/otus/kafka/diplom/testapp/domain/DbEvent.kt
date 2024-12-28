package ru.otus.kafka.diplom.testapp.domain

import java.time.OffsetDateTime
import java.util.UUID

data class DbEvent(
    val processId: UUID,
    val eventTime: OffsetDateTime,
    val resultCode: ResultCode,
)
