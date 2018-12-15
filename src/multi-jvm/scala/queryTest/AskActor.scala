package queryTest

import akka.actor.{Actor, Address, Props}
import akka.cluster.Cluster
import akka.messenger.api.Connector
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object AskActor {
  def props(id: String): Props = Props(new AskActor(id))
}

class AskActor(private val id: String) extends Actor {
  var connector: Option[Connector] = None
  var cluster: Option[Cluster] = None

  override def preStart(): Unit = {
    implicit val ec: ExecutionContext = context.system.dispatcher
    context.system.scheduler.scheduleOnce(10.seconds) {
      cluster = Some(Cluster(context.system))
      cluster.foreach(c => c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552)))
    }

    context.system.scheduler.scheduleOnce(15.seconds) {
      connector = Some(Connector.make(s"ask-svc-$id")(context.system))
    }

    context.system.scheduler.scheduleOnce(20.seconds) {
      connector.foreach { c =>
        implicit val timeout: FiniteDuration = 2.seconds
        c.askQuery(toServiceName = "answer-svc", EchoQuery(message = s"hello from ask-svc-$id")).map {
          case EchoEvent(message) =>
            println(s"ask-svc-$id - Echo event: $message")
          case _ =>
            println(s"ask-svc-$id - Unknown event")
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
