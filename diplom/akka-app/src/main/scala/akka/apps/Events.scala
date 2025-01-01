package akka.apps


import java.util.UUID
import scala.collection.mutable


object ResultCode extends Enumeration {
  val OK: Value = Value("200")
  val BAD_REQUEST: Value = Value("400")
  val INTERNAL_SERVER_ERROR: Value = Value("500")
}

object EventType extends Enumeration {
  val EventType1000: Value = Value("1000")
  val EventType2000: Value = Value("2000")
}


sealed abstract class Event(val eventType: EventType.Value, val processId: UUID, val eventTime: Long)

case class Event1000(override val processId: UUID,
                     override val eventTime: Long, endpoint: String) extends Event(EventType.EventType1000, processId, eventTime)

case class Event2000(override val processId: UUID,
                     override val eventTime: Long, status: ResultCode.Value) extends Event(EventType.EventType2000, processId, eventTime)

// Приходят из топика по данным БД
case class DbEvent(processId: UUID, eventTime: Long, status: ResultCode.Value)

// Приходят из kafka по данным БД
case class KafkaEvent(processId: UUID, eventTime: Long, endpoint: String)

case class WindowEvents(w: Window, event: mutable.Set[Event])

case class MergedEvent(messageId: UUID, name: String, delta: Long, status: ResultCode.Value)

case class TimedEvent(eventTime: Long)
