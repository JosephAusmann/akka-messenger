package eventGroupTest

import akka.actor.{Actor, Address, Props}
import akka.cluster.Cluster

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object MessengerClusterActor {
  def props(firstNode: Boolean): Props = Props(new MessengerClusterActor(firstNode))
}

class MessengerClusterActor(private val firstNode: Boolean) extends Actor {

  var cluster: Option[Cluster] = None

  override def preStart(): Unit = {
    implicit val ec: ExecutionContext = context.system.dispatcher
    val cluster = Some(Cluster(context.system))
    cluster.foreach { c =>
      if (firstNode) {
        c.join(c.selfAddress)
      } else {
        context.system.scheduler.scheduleOnce(5.seconds) {
          c.join(Address(protocol = "akka", system = akka.messenger.api.systemName, host = "127.0.0.1", port = 2552))
        }
      }
    }

    import akka.messenger.actors.MessengerActor
    context.system.actorOf(MessengerActor.props, akka.messenger.api.systemName)

    context.system.scheduler.scheduleOnce(25.seconds) {
      cluster.foreach(c => c.down(c.selfAddress))
    }
  }

  override def receive: Receive = {
    case _ =>
  }
}

