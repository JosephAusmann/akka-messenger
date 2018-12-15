package commandTest

import akka.actor.{Actor, Address, Props}
import akka.cluster.Cluster
import akka.messenger.api.Connector
import akka.messenger.api.exceptions.UnknownCommand
import scala.concurrent.Future
import scala.concurrent.duration._

object AnswerActor {
  def props: Props = Props[AnswerActor]
}

class AnswerActor extends Actor {
  var connector: Option[Connector] = None
  var cluster: Option[Cluster] = None
  private implicit val ec = context.system.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(10.seconds) {
      cluster = Some(Cluster(context.system))
      cluster.foreach(c => c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552)))
    }

    context.system.scheduler.scheduleOnce(15.seconds) {
      connector = Some(Connector.make("answer-svc")(context.system))

      connector.foreach { c =>
        c.installCommandHandlerFunction {
          case EchoCommand(message) =>
            Future {
              println(s"answer-svc - Echo Command: $message")
              EchoEvent(message = message)
            }
          case _ =>
            Future {
              println("answer-svc - Unknown command")
              throw UnknownCommand()
            }
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

