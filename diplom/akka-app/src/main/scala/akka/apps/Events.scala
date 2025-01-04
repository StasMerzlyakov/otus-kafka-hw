package akka.apps


import java.util.UUID
import scala.collection.mutable


object ResultCode extends Enumeration {
  val OK: Value = Value("200")
  val BAD_REQUEST: Value = Value("400")
  val INTERNAL_SERVER_ERROR: Value = Value("500")
}

sealed trait Event

case class Event1000(processId: UUID, eventTime: Long, endpoint: String) extends Event

case class Event2000(processId: UUID, eventTime: Long, status: ResultCode.Value) extends Event

// Приходят из топика по данным БД
case class DbEvent(processId: UUID, eventTime: Long)

// Приходят из kafka по данным БД
case class KafkaEvent(processId: UUID, eventTime: Long, endpoint: String)

case class EventChunk(w: Window, event: mutable.Map[UUID, (Event1000, Event2000)])

case class TimedEvent(eventTime: Long)

case class EndpointSpeed(endpoint: String,
                         tps: Double, // average speed (all requests per win duration)
                         avg: Double) // average single request duration


