package akka.apps

import akka.NotUsed
import akka.actor.ActorSystem
import akka.apps.kafka.{KafkaProducer, KafkaSource}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Source}
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt

object MainApp {
  implicit val system: ActorSystem = ActorSystem("analyzer")
  implicit val logger: Logger = LoggerFactory.getLogger(getClass)
  val config = ConfigFactory.load()
  private val windowStep = config.getDuration("akka.windowStep", TimeUnit.MILLISECONDS)
  private val windowLength = config.getDuration("akka.windowLength", TimeUnit.MILLISECONDS)

  private val windowsCommandSource = Source
    .tick(0.seconds, 1.second, "tick")
    .map { _ =>
      val now = System.currentTimeMillis()
      TimedEvent(now)
    }.statefulMapConcat { () =>
    ev => CommandGenerator.forEvent(ev, windowLength, windowStep)
  }

  def speedInfo(es: EndpointSpeed): EndpointSpeed = {
    logger.info(s"${es}")
    es
  }

  val graph =
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      // TODO - переделать на graph
      val source = KafkaSource.input.merge(windowsCommandSource).statefulMapConcat { () =>
        cd => EventCollector.forEvent(cd)
      }.statefulMapConcat(() =>
        ev => Aggregator.forEvent(ev)
      )
      val input = builder.add(source)

      val logging = builder.add(Flow[EndpointSpeed].map(es => speedInfo(es)))

      val mapToProducerRecord = KafkaProducer.mapToProducerRecord

      val kafkaSink = KafkaProducer.kafkaSink

      input ~> logging ~> mapToProducerRecord ~> kafkaSink

      ClosedShape
    }

  def main(args: Array[String]): Unit = {

    RunnableGraph.fromGraph(graph).run()


  }
}

