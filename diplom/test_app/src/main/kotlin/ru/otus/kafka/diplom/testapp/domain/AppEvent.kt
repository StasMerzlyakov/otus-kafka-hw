package ru.otus.kafka.diplom.testapp.domain

import java.time.OffsetDateTime
import java.util.UUID

data class AppEvent(
    val timestamp: OffsetDateTime,
    val processId: UUID,
)
