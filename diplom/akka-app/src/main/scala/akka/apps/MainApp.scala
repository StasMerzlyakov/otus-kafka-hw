package akka.apps

import akka.actor.ActorSystem
import akka.apps.kafka.KafkaSource.{config, input}
import akka.stream.scaladsl.Source
import org.slf4j.{Logger, LoggerFactory}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt

object MainApp {
  implicit val system: ActorSystem = ActorSystem("analyzer")
  implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  private val windowStep = config.getDuration( "akka.windowStep", TimeUnit.MILLISECONDS)
  private val windowLength = config.getDuration( "akka.windowLength", TimeUnit.MILLISECONDS)

  private val windowsCommandSource = Source
    .tick(0.seconds, 1.second, "tick")
    .map { _ =>
      val now = System.currentTimeMillis()
      TimedEvent(now)
    }.statefulMapConcat { () =>
    ev => CommandGenerator.forEvent(ev, windowLength, windowStep)
  }

  def main(args: Array[String]): Unit = {

    val mergedSource = input.merge(windowsCommandSource)

    mergedSource.statefulMapConcat { () =>
      cd => EventCollector.forEvent(cd)
    }.statefulMapConcat(() =>
      ev => Aggregator.forEvent(ev)
    ).runForeach(es => system.log.info(s"$es"))

  }
}

