package ru.otus.kafka.diplom.testapp

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.UUIDSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.otus.kafka.diplom.testapp.domain.KafkaEvent
import java.util.UUID

@Configuration
class Configuration {

    @Autowired
    private lateinit var kafkaProperties: KafkaProperties

    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

    @Bean
    fun producerFactory(): ProducerFactory<UUID, KafkaEvent> = kafkaProperties.buildProducerProperties().let {
        it[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        DefaultKafkaProducerFactory(
            it,
            UUIDSerializer(),
            KafkaEventSerializer(objectMapper()),
        )
    }

    class KafkaEventSerializer(objectMapper: ObjectMapper) : JsonSerializer<KafkaEvent>(objectMapper)

    @Bean
    fun kafkaTemplate(): KafkaTemplate<UUID, KafkaEvent> {
        return KafkaTemplate(producerFactory())
    }
}
