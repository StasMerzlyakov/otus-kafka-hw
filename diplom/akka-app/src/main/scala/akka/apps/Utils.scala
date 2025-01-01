package akka.apps

import java.time.{Instant, OffsetDateTime, ZoneId}
import scala.collection.immutable

object Utils {
  def tsToString(ts: Long): String = OffsetDateTime
    .ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
    .toLocalTime
    .toString
  def toImmutable[A](elements: Iterable[A]): immutable.Iterable[A] =
    new scala.collection.immutable.Iterable[A] {
      override def iterator: Iterator[A] = elements.toIterator
    }

}