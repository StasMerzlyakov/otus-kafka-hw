package akka.apps

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.scaladsl.Flow
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.mutable


sealed trait CommandDispatcher
private case class Open(wc: OpenWindow) extends CommandDispatcher
private case class Close(cw: CloseWindow, replyTo: WindowEvents) extends CommandDispatcher

private case class Signal(event: Event) extends CommandDispatcher


class TimedFlow extends GraphStage[FlowShape[(Event, WindowCommand), WindowEvents]] {

  val in: Inlet[(Event, WindowCommand)] = Inlet[(Event, WindowCommand)]("ZipperFlow.in")
  private val out = Outlet[WindowEvents]("ZipperFlow.out")

  override val shape: FlowShape[(Event, WindowCommand), WindowEvents] = FlowShape.of(in, out)

  private val openWindows = mutable.Set[WindowEvents]()

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    setHandler(in, new InHandler {
      override def onPush(): Unit = {



        push(out, DataOut("content-" + grab(in)._1.id))
      }
    })
    setHandler(out, new OutHandler {
      override def onPull(): Unit = {
        pull(in)
      }
    })

  }



}



/**
 * Принимает на вход Event и WindowCommand. Добавляет Events к активным окнам.
 */
object EventCollector {

  private val openWindows = mutable.Set[WindowEvents]()

  def apply(): Behavior[CommandDispatcher] = Behaviors.setup{ ctx =>
    Behaviors.receiveMessage {
      case Open(ow) =>
        openWindows.add(WindowEvents(ow.w, mutable.Set[Event]()))
        Behaviors.same
      case Close(cw, replyTo) =>
        val we = openWindows.filter(we => we.w == cw.w).head
        if (we !=null) {
          openWindows.remove(we)
          ctx.spawn(we, "WindowsEvent")
        }
        Behaviors.same
      case Signal(event) =>
        openWindows.foreach(ow => ow.event.add(event))
        Behaviors.same
    }
  }
}




