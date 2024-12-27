package akka.apps

import akka.NotUsed
import akka.stream.scaladsl.{GraphDSL, Sink, Source, ZipN}
import akka.stream.{ClosedShape, Graph}

object MainApp {
  /*implicit val system = ActorSystem("analyzer")
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
    } */

  // Отделяем детали реализации DSL от реализации входных и выходных потоков
  def createGraph(
                   maxDelta: Long,
                   dbInput1: Source[DBEvent, NotUsed],
                   appInput: Source[AppEvent, NotUsed],
                   successOutput: Sink[MergedEvent, NotUsed], // статус OK и delta < maxDelta
                   tooLongOutput: Sink[MergedEvent, NotUsed], // статус OK и delta == null || delta > maxDelta
                   badReqOutput: Sink[MergedEvent, NotUsed],  // статус BadRequest
                   intErrOutput: Sink[MergedEvent, NotUsed],  // статус InternalServerError
                 ): Graph[ClosedShape, NotUsed] = {
    GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>

        val input1 = builder.add(dbInput1)
        val input2 = builder.add(appInput)

        val zip = builder.add(ZipN[Int](3))


        val output1 = builder.add(successOutput)
        val output2 = builder.add(tooLongOutput)
        val output3 = builder.add(badReqOutput)
        val output4 = builder.add(intErrOutput)


        ClosedShape
    }
  }


  /*def create[S <: Shape]()(buildBlock: GraphDSL.Builder[NotUsed] => S): Graph[S, NotUsed] = {
    val builder = new GraphDSL.Builder
    val s = buildBlock(builder)

    createGraph(s, builder)
  }
*/
  def main(args: Array[String]): Unit = {
    //  RunnableGraph.fromGraph(createGraph()).run()
  }
}
