package akka.apps.kafka

import akka.apps.EndpointSpeed
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Flow
import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}


object KafkaProducer {
  private val config = ConfigFactory.load()
  private val producerConfig = config.getConfig("akka.kafka.producer")

  private val resultTopicName = config.getString("akka.kafka.producer.output-topic")

  private val objectMapper = new ObjectMapper()
  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
  objectMapper.registerModule(new DefaultScalaModule())

  private val producerSettings = ProducerSettings(producerConfig, new StringSerializer, EndpointSpeedSerializer(objectMapper))


  val mapToProducerRecord = Flow[EndpointSpeed].map(elem => new ProducerRecord[String, EndpointSpeed](resultTopicName, elem))

  val kafkaSink = Producer.plainSink(producerSettings)

}


case class Field(@JsonProperty("type")
                 fldType: String,
                 optional: Boolean,
                 field: String,
                )

case class EndpointSchema(@JsonProperty("type")
                          schType: String,
                          fields: List[Field],
                         )
case class EndpointSpeedWithSchema(
                                    schema: EndpointSchema,
                                    payload: EndpointSpeed)

// schema json add
case class EndpointSpeedSerializer(private val objectMapper: ObjectMapper) extends Serializer[EndpointSpeed] {
  override def serialize(topic: String, data: EndpointSpeed): Array[Byte] = {
    val schema = EndpointSchema(
      schType = "struct",
      fields = List(
        Field(fldType = "string", optional = false, field = "endpoint"),
        Field(fldType = "double", optional = false, field = "tps"),
        Field(fldType = "double", optional = false, field = "avg"),
      )
    )
    val toJson = EndpointSpeedWithSchema(schema = schema, payload = data)
    objectMapper.writeValueAsBytes(toJson)
  }
}
