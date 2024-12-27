package ru.otus.kafka.diplom.testapp.domain

import java.time.OffsetDateTime
import java.util.UUID

data class DbEvent(
    val timestamp: OffsetDateTime,
    val processId: UUID,
    val resultCode: ResultCode,
)
