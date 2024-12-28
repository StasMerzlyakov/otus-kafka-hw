package ru.otus.kafka.diplom.testapp.domain

import java.time.OffsetDateTime
import java.util.UUID

data class AppEvent(
    val processId: UUID,
    val eventTime: OffsetDateTime,
    val endpoint: String,
)
