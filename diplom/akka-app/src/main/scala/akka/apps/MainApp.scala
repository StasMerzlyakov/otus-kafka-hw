package akka.apps

import akka.NotUsed
import Utils.tsToString
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipN}
import akka.stream.{ActorMaterializer, ClosedShape, Graph, Materializer}
import org.slf4j.LoggerFactory
import akka.stream.scaladsl.Source.tick

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.util.Random

object MainApp {
  implicit val system = ActorSystem("analyzer")
  implicit val logger = LoggerFactory.getLogger(getClass)

  def errInfo(x: Any): Unit = logger.error("{}", x)

  val tickerSource = Source
    .tick(0.seconds, 1.second, "tick")
    .map { _ =>
      val now = System.currentTimeMillis()
      TimedEvent(now)
    }.statefulMapConcat { () =>
      val generator = new CommandGenerator()
      ev => generator.forEvent(ev)
    }

  val entryPointList = Array(
    "browsedrive.gov/accept",
    "skiptube.gov",
    "vitz.mil:8074",
    "teklist.net/do/hello",
    "linktype.com:12345",
    "cogilith.info/receive",
  )

  //Random.shuffle(entryPointList.toList).head

  val eventSource =
    tick(0.seconds, 500.milliseconds, "event")
      .statefulMapConcat { () => {
        _ =>
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

  val graph =
    GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._

        val inputEvents = builder.add(eventSource)
        val inputTicker = builder.add(tickerSource)

        val eventCollector = builder.add(EventCollector)

        val output = builder.add(Sink.foreach(errInfo))


        inputEvents ~> eventCollector
        inputTicker ~> eventCollector
        eventCollector ~> output

        ClosedShape

    }


  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(graph).run()
  }
}

