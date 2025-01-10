package ru.otus.kafka.diplom.testapp.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class KafkaEvent(
    @JsonProperty("process_id")
    val processId: UUID,
    @JsonProperty("event_time")
    val eventTime: OffsetDateTime,
    @JsonProperty("endpoint")
    val endpoint: String,
    @JsonProperty("event_type")
    val eventType: EventType,
)
