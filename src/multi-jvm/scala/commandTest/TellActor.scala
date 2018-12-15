package commandTest

import akka.actor.{Actor, Address, Props}
import akka.cluster.Cluster
import akka.messenger.api.Connector
import scala.concurrent.duration._

object TellActor {
  def props(id: String): Props = Props(new TellActor(id))
}

class TellActor(private val id: String) extends Actor {
  var connector: Option[Connector] = None
  var cluster: Option[Cluster] = None
  private implicit val ec = context.system.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(10.seconds) {
      cluster = Some(Cluster(context.system))
      cluster.foreach(c => c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552)))
    }

    context.system.scheduler.scheduleOnce(15.seconds) {
      connector = Some(Connector.make(s"tell-svc-$id")(context.system))

      connector.foreach { c =>
        c.installEventHandlerFunction {
          case EchoEvent(message) =>
            println(s"tell-svc-$id - Notified of event: $message")
          case _ =>
            println(s"tell-svc-$id - Unknown event")
        }

        implicit val timeout: FiniteDuration = 2.seconds
        c.subscribeToServiceEvents("answer-svc")
      }
    }

    context.system.scheduler.scheduleOnce(20.seconds) {
      connector.foreach { c =>
        implicit val timeout: FiniteDuration = 2.seconds
        c.tellCommand(toServiceName = "answer-svc", EchoCommand(message = s"hello from tell-svc-$id")).map {
          case EchoEvent(message) =>
            println(s"tell-svc-$id - Echo event: $message")
          case _ =>
            println(s"tell-svc-$id - Unknown event")
        }.recover {
          case e: Exception =>
            println(e.toString)
        }
      }
    }

    context.system.scheduler.scheduleOnce(25.seconds) {
      cluster.foreach(c => c.down(c.selfAddress))
    }
  }

  override def postStop(): Unit = {
  }

  override def receive: Receive = {
    case _: Any => ()
  }
}
