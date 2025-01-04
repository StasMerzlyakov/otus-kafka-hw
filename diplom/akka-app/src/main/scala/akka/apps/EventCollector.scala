package akka.apps

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.apps.CommandGenerator.{openWindows, watermark}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.scaladsl.Flow
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import java.util.UUID
import scala.collection.mutable


sealed trait CommandDispatcher

private case class Open(w: Window) extends CommandDispatcher

private case class Close(w: Window) extends CommandDispatcher

private case class Signal1000(processId: UUID, eventTime: Long, endpoint: String) extends CommandDispatcher

private case class Signal2000(processId: UUID, eventTime: Long, status: ResultCode.Value) extends CommandDispatcher


/**
 * Добавляет Events к активным окнам.
 * TODO добавить поправку на случай, когда результат (Signal2000) приходит после закрытия окна.
 */
object EventCollector {

  private val openWindows = mutable.Set[EventChunk]()

  def forEvent(cd: CommandDispatcher): List[EventChunk] = {
    cd match {
      case Open(w) =>
        openWindows.add(EventChunk(w, mutable.Map[UUID, (Event1000, Event2000)]()))
        Nil
      case Close(w) =>
        val eventChunk = openWindows.flatMap { we =>
          if (we.w == w) {
            openWindows.remove(we)
            Some(we)
          } else Nil
        }
        eventChunk.toList
      case Signal1000(processId, eventTime, endpoint) =>
        openWindows.foreach(ow =>
          if (!ow.event.contains(processId)) {
            ow.event.put(processId, (Event1000(processId, eventTime, endpoint), null))
          } else {
            // todo duplicate processing
          })
        Nil
      case Signal2000(processId, eventTime, status) =>
        openWindows.foreach(ow =>
          if (ow.event.contains(processId)) {
            val currentVal = ow.event(processId)
            if (currentVal._2 == null) {
              ow.event.put(processId, (currentVal._1, Event2000(processId, eventTime, status)))
            } else {
              // ignore duplicate
            }
          } else {
            ow.event.put(processId, (null, Event2000(processId, eventTime, status)))
          })
        Nil
    }
  }
}




