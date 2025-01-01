package akka.apps

import scala.concurrent.duration.DurationInt

case class Window(from: Long, to: Long) {
  override def toString = s"From ${Utils.tsToString(from)} to ${Utils.tsToString(to)}"
}

object Window {
  // TODO - константы в конфига
  val WindowLength    = 30.seconds.toMillis
  val WindowStep      =  10.second .toMillis
  val WindowsPerEvent = (WindowLength / WindowStep).toInt

  def windowsFor(ts: Long): Set[Window] = {
    val firstWindowStart = ts - ts % WindowStep - WindowLength + WindowStep
    (for (i <- 0 until WindowsPerEvent) yield Window(
      firstWindowStart + i * WindowStep,
      firstWindowStart + i * WindowStep + WindowLength)
    ).toSet
  }
}
