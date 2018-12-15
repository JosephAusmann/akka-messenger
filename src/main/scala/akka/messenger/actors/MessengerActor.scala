package akka.messenger.actors

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Send}
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import akka.messenger.messages._

object MessengerActor {
  def props: Props = Props[MessengerActor]
}

private final class MessengerActor extends Actor {

  private val mediator = DistributedPubSub(context.system).mediator
  mediator ! DistributedPubSubMediator.Put(self)

  override def receive: Receive = {
    case askQuery: AskQuery =>
      mediator ! Send(path = s"/user/${askQuery.toServiceName}", msg = askQuery, localAffinity = false)
    case answerQuery: AnswerQuery =>
      answerQuery.toNodeRef.foreach(_ ! answerQuery)
    case tellCommand: TellCommand =>
      mediator ! Send(path = s"/user/${tellCommand.toServiceName}", msg = tellCommand, localAffinity = false)
    case answerCommand: AnswerCommand =>
      answerCommand.toNodeRef.foreach(_ ! answerCommand)
    case notifyEvent: NotifyEvent =>
      mediator ! Publish(topic = notifyEvent.fromServiceName, msg = notifyEvent)
  }
}
