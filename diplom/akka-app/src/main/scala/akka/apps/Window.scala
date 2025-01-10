package akka.apps


case class Window(from: Long, to: Long) {
  override def toString = s"From ${Utils.tsToString(from)} to ${Utils.tsToString(to)}"
}

object Window {
  def windowsFor(ts: Long, windowLength: Long, windowStep: Long): Set[Window] = {
    val WindowsPerEvent = (windowLength / windowStep).toInt
    val firstWindowStart = ts - ts % windowStep - windowLength + windowStep
    (for (i <- 0 until WindowsPerEvent) yield Window(
      firstWindowStart + i * windowStep,
      firstWindowStart + i * windowStep + windowLength)
    ).toSet
  }
}
