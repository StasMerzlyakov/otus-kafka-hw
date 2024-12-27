package akka.apps

import java.util.UUID


object ResultCode extends Enumeration {
  val OK = Value("200")
  val BAD_REQUEST = Value("400")
  val INTERNAL_SERVER_ERROR = Value("500")
}

case class DBEvent (timestamp: Long, messageId: UUID, status: ResultCode.Value)

case class AppEvent (timestamp: Long, messageId: UUID, name: String)

case class MergedEvent(messageId: UUID, name: String, delta: Long, status: ResultCode.Value)