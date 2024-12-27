package ru.otus.kafka.diplom.testapp

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import ru.otus.kafka.diplom.testapp.domain.AppEvent


@Configuration
class Configuration {

    @Autowired
    private lateinit var kafkaProperties: KafkaProperties

    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper
    }

    @Bean
    fun producerFactory(): ProducerFactory<String?, AppEvent> =
        kafkaProperties.buildProducerProperties().let {
            it[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
            DefaultKafkaProducerFactory(it)
        }


    @Bean
    fun kafkaTemplate(): KafkaTemplate<String?, AppEvent> {
        return KafkaTemplate(producerFactory())
    }

}