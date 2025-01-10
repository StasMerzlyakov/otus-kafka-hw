package akka.apps

import java.util.UUID
import scala.collection.mutable


/**
 * Добавляет Events к активным окнам.
 */
object EventCollector {

  private val openWindows = mutable.Set[EventChunk]()

  def forEvent(cd: Any): List[EventChunk] = {
    cd match {
      case OpenWindow(w) =>
        openWindows.add(EventChunk(w, mutable.Map[UUID, (Event1000, Event2000)]()))
        Nil
      case CloseWindow(w) =>
        val eventChunk = openWindows.flatMap { we =>
          if (we.w == w) {
            openWindows.remove(we)
            Some(we)
          } else Nil
        }
        eventChunk.toList
      case event : Event1000 =>
        val processId = event.processId
        openWindows.foreach(ow =>
          if (!ow.event.contains(processId)) {
            ow.event.put(processId, (event, null))
          } else {
            // todo duplicate processing
          })
        Nil
      case event: Event2000 =>
        val processId = event.processId
        openWindows.foreach(ow =>
          if (ow.event.contains(processId)) {
            val currentVal = ow.event(processId)
            if (currentVal._2 == null) {
              ow.event.put(processId, (currentVal._1, event))
            } else {
              // ignore duplicate
            }
          } else {
            ow.event.put(processId, (null, event))
          })
        Nil
    }
  }
}




