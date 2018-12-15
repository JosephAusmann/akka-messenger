package eventGroupTest

import akka.actor.{Actor, Address}
import akka.cluster.Cluster
import akka.messenger.api.Connector

import scala.concurrent.duration._

class NotifyEventActor extends Actor {

  var connector: Option[Connector] = None
  var cluster: Option[Cluster] = None
  private implicit val ec = context.system.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(10.seconds) {
      cluster = Some(Cluster(context.system))
      cluster.foreach(c => c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552)))
    }

    context.system.scheduler.scheduleOnce(15.seconds) {
      connector = Some(Connector.make(s"notify-svc")(context.system))
    }

    context.system.scheduler.scheduleOnce(20.seconds) {
      connector.foreach { c =>
        for (i <- 0 to 10) {
          c.notifyEvent(CounterEvent(counter = i))
        }
      }
    }

    context.system.scheduler.scheduleOnce(25.seconds) {
      cluster.foreach(c => c.down(c.selfAddress))
    }
  }

  override def receive: Receive = {
    case _: Any =>
  }
}
