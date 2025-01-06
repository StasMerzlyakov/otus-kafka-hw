package akka.apps.kafka

import akka.apps.{Event1000, Event2000, ResultCode}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, JsonScalaEnumeration}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.{Deserializer, UUIDDeserializer}
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory

import java.io.{PrintWriter, StringWriter}
import java.time.OffsetDateTime
import java.util.UUID

object KafkaSource {
  //implicit val system: ActorSystem = ActorSystem("consumer-sys")
  //implicit val mat: Materializer = ActorMaterializer()
  //implicit val ec: ExecutionContextExecutor = system.dispatcher
  LoggerFactory
    .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[Logger]
    .setLevel(Level.INFO)

  val config = ConfigFactory.load()
  val consumerConfig = config.getConfig("akka.kafka.consumer")
  val topicName = config.getString("akka.kafka.consumer.topic")


  val objectMapper = new ObjectMapper()
  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
  objectMapper.registerModule(new JavaTimeModule())
  objectMapper.registerModule(new DefaultScalaModule())
  objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)


  val consumerSettings = ConsumerSettings(consumerConfig,
    new UUIDDeserializer(),
    CombinedEventDeserializer(objectMapper))


  val input = Consumer
    .plainSource(consumerSettings, Subscriptions.topics(topicName))
    .map(consumerRecord =>
      if (consumerRecord.value().eventType == EventType.Event1000) {
        Event1000(consumerRecord.value().processId,
          consumerRecord.value().eventTime,
          consumerRecord.value().endpoint,
        )
      } else {
        Event2000(consumerRecord.value().processId,
          consumerRecord.value().eventTime,
          consumerRecord.value().resultCode
        )
      }
    )
}

// Разбор входящих сообщений. Добиваемся потока из объектов akka.apps.Event1000 и akka.apps.Event2000
object EventType extends Enumeration {
  type EventType = Value
  val Event1000: Value = Value("Event1000")
  val Event2000: Value = Value("Event2000")
}

class EventTypeHolder extends TypeReference[EventType.type]

class ResultCodeHolder extends TypeReference[ResultCode.type]

case class CombinedEvent(
                         @JsonScalaEnumeration(classOf[EventTypeHolder])
                         @JsonProperty("event_type")
                         val eventType: EventType.EventType,
                         @JsonProperty("process_id")
                         val processId: UUID,
                         @JsonProperty("event_time")
                         val eventTime: OffsetDateTime,
                         @JsonProperty("endpoint")
                         val endpoint: String,
                         @JsonScalaEnumeration(classOf[ResultCodeHolder])
                         @JsonProperty("result_code")
                         val resultCode: ResultCode.ResultCode)

case class CombinedEventDeserializer(private val objectMapper: ObjectMapper) extends Deserializer[CombinedEvent] {

  implicit val logger = LoggerFactory.getLogger(getClass)

  override def deserialize(topic: String, data: Array[Byte]): CombinedEvent = {
    try {
      objectMapper.readValue(data, classOf[CombinedEvent])
    } catch {
      case ex: Exception =>
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        logger.error(sw.toString)
        throw ex
    }
  }
}




