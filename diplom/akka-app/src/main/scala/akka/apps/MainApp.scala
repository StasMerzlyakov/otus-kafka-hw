package akka.apps

import akka.NotUsed
import Utils.tsToString
import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipN}
import akka.stream.{ActorMaterializer, ClosedShape, Graph, Materializer}
import org.slf4j.{Logger, LoggerFactory}
import akka.stream.scaladsl.Source.tick

import java.util.UUID
import scala.collection.Set
import scala.concurrent.duration.DurationInt
import scala.util.Random

object MainApp {
  implicit val system: ActorSystem = ActorSystem("analyzer")
  implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  private val windowStep = 10.seconds.toMillis // TODO в конфиг
  private val windowLength = 30.seconds.toMillis

  private val windowsCommandSource = Source
    .tick(0.seconds, 1.second, "tick")
    .map { _ =>
      val now = System.currentTimeMillis()
      TimedEvent(now)
    }.statefulMapConcat { () =>
      ev => CommandGenerator.forEvent(ev, windowLength, windowStep)
    }

  private val entryPointList = Array(
    "browsedrive.gov/accept",
    "skiptube.gov",
    "vitz.mil:8074",
    "teklist.net/do/hello",
    "linktype.com:12345",
    "cogilith.info/receive",
  )

  //Random.shuffle(entryPointList.toList).head

  private val eventSource =
    tick(0.seconds, 500.milliseconds, "event")
      .statefulMapConcat { () => {
        _ =>
          // TODO
          val now = System.currentTimeMillis()
          val delay = Random.nextInt(8)
          val url = Random.shuffle(entryPointList.toList).head
          val rndInt = Random.nextInt(40)
          val resultCode = rndInt match {
            case 0 => ResultCode.BAD_REQUEST
            case 1 => ResultCode.INTERNAL_SERVER_ERROR
            case _ => ResultCode.OK
          }

          val processId = UUID.randomUUID()
          val startEvent = Event1000(processId, now, url)
          val endEvent = Event2000(processId, now + delay * 500, resultCode)
          startEvent :: endEvent :: Nil
      }
      }


  def main(args: Array[String]): Unit = {
    val winCommandSource = windowsCommandSource.map {
      case OpenWindow(w) =>
        Open(w)
      case CloseWindow(w) =>
        Close(w)
    }

    val signalSource = eventSource.map {
      case Event1000(processId, eventTime, endpoint) =>
        Signal1000(processId, eventTime, endpoint)
      case Event2000(processId, eventTime, status) =>
        Signal2000(processId, eventTime, status)
    }

    val mergedSource = signalSource.merge(winCommandSource)

    mergedSource.statefulMapConcat { () =>
      cd => EventCollector.forEvent(cd)
    }.statefulMapConcat( () =>
      ev => Aggregator.forEvent(ev)
    ).runForeach(es => system.log.info(s"$es"))

  }
}

