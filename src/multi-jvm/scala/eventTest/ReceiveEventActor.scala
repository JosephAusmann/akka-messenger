package eventTest

import akka.actor.{Actor, Address, Props}
import akka.cluster.Cluster
import akka.messenger.api.Connector
import scala.concurrent.duration._

object ReceiveEventActor {
  def props(id: Int): Props = Props(new ReceiveEventActor(id))
}

class ReceiveEventActor(id: Int) extends Actor {

  var connector: Option[Connector] = None
  var cluster: Option[Cluster] = None
  private implicit val ec = context.system.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(10.seconds) {
      cluster = Some(Cluster(context.system))
      cluster.foreach(c => c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552)))
    }

    context.system.scheduler.scheduleOnce(15.seconds) {
      connector = Some(Connector.make("receive-svc")(context.system))

      connector.foreach { c =>
        c.installEventHandlerFunction {
          case CounterEvent(counter) =>
            println(s"receive-svc-$id - Counter Event: $counter")
          case _ =>
            println(s"receive-svc-$id - Unknown event")
        }

        c.subscribeToServiceEvents("notify-svc")
      }
    }

    context.system.scheduler.scheduleOnce(25.seconds) {
      cluster.foreach(c => c.down(c.selfAddress))
    }
  }

  override def postStop(): Unit = {
  }

  override def receive: Receive = {
    case _ =>
  }

}
