package akka.apps

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.duration.Duration

// TODO resultCode processing
object Aggregator {

  def forEvent(ev: EventChunk): List[EndpointSpeed] = {

    val winSize = ev.w.to - ev.w.from

    val tpsCount = mutable.Map[String, Int]().withDefault(_ => 0)

    // tps evaluation
    ev.event
      .filter { case (_, (event1000, event2000)) => event1000 != null && event2000 != null }
      .foreach { case (_, (event1000, _)) =>
        val current = tpsCount(event1000.endpoint)
        tpsCount(event1000.endpoint) = current + 1
      }

    // average evaluation
    val averageCount = mutable.Map[String, (Long, Int)]().withDefault(_ => (0, 0))
    ev.event
      .filter { case (_, (event1000, event2000)) => event1000 != null && event2000 != null }
      .foreach {
        case (_, (event1000, event2000)) =>
          val current = averageCount(event1000.endpoint)
          averageCount(event1000.endpoint) = (current._1 + event2000.eventTime - event1000.eventTime, current._2 + 1)
      }

    val endpointSpeed = tpsCount.flatMap{
      case (endpoint, count) =>
        val tpsSpeed = count.toDouble  / winSize * 1000 // per second
        val (allDuration, allCount) = averageCount(endpoint)
        val avgSpeed: Double = if (allCount > 0) {
          allDuration.toDouble / allCount / 1000 // duration in second
        } else 0
        Some(EndpointSpeed(endpoint, tpsSpeed, avgSpeed))
    }

    endpointSpeed.toList
  }
}
