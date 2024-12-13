package akka_akka_streams.homework

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipN}
import org.slf4j.LoggerFactory


object GraphApp {
  implicit val system = ActorSystem("fusion")
  implicit val materializer = ActorMaterializer()

  implicit val logger = LoggerFactory.getLogger(getClass)

  def errInfo(x: Any): Unit = logger.error("{}", x)

  val graph =
    GraphDSL.create(){ implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val input = builder.add(KafkaSource.input)

      val multiplier10 = builder.add(Flow[Int].map(x=>x*10))
      val multiplier2 = builder.add(Flow[Int].map(x=>x*2))
      val multiplier3 = builder.add(Flow[Int].map(x=>x*3))

      val output = builder.add(Sink.foreach(errInfo))

      val broadcast = builder.add(Broadcast[Int](3))

      val zip = builder.add(ZipN[Int](3))

      val appender = builder.add(Flow[Seq[Int]].map(x => x.sum))


      //3
      input ~> broadcast

      broadcast.out(0) ~> multiplier10 ~> zip.in(0)
      broadcast.out(1) ~> multiplier2 ~> zip.in(1)
      broadcast.out(2) ~> multiplier3 ~> zip.in(2)

      zip ~> appender.in

      appender.out ~> output

      //4
      ClosedShape
    }


  def main(args: Array[String]) : Unit ={
    RunnableGraph.fromGraph(graph).run()
  }
}