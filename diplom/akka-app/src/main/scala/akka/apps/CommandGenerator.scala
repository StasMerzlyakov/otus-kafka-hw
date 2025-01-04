package akka.apps

import scala.collection.mutable

sealed trait WindowCommand {
  def w: Window
}

case class OpenWindow(w: Window) extends WindowCommand
case class CloseWindow(w: Window) extends WindowCommand

/**
 * По входному потоку сообщений от таймера формирует поток команд на открытие/закрытие окон
 */
object CommandGenerator {

  private val openWindows = mutable.Set[Window]()
  private var watermark = 0L

  def forEvent(ev: TimedEvent, windowLength: Long, windowStep: Long): List[WindowCommand] = {
    watermark = math.max(watermark, ev.eventTime - windowStep)
    if (ev.eventTime < watermark) {
      Nil
    } else {
      val eventWindows = Window.windowsFor(ev.eventTime, windowLength, windowStep)
      val closeCommands = openWindows.flatMap { ow =>
        if (!eventWindows.contains(ow) && ow.to < watermark) {
          openWindows.remove(ow)
          Some(CloseWindow(ow))
        } else None
      }

      val openCommands = eventWindows.flatMap { w =>
        if (!openWindows.contains(w)) {
          openWindows.add(w)
          Some(OpenWindow(w))
        } else None
      }

      openCommands.toList ++ closeCommands.toList
    }
  }
}

