package akka.messenger.actors

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Send, Subscribe, Unsubscribe}
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import akka.messenger.api.messages.{Command, Event, Query}
import akka.messenger.messages._
import akka.messenger.api.exceptions.RequestTimeout
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{Future, Promise}

private[messenger] object ConnectorActor {
  def props(serviceName: String): Props = Props {
    new ConnectorActor(serviceName)
  }
}

private final class ConnectorActor(private val thisServiceName: String) extends Actor {

  private case class RestoreState(queryHandler: Option[PartialFunction[Query, Future[Event]]],
                                  commandHandler: Option[PartialFunction[Command, Future[Event]]],
                                  eventHandler: Option[PartialFunction[Event, Unit]],
                                  requests: mutable.HashMap[String, Promise[Event]])

  private case class TimeoutRequestPromise(requestId: String)

  private val mediator = DistributedPubSub(context.system).mediator
  mediator ! DistributedPubSubMediator.Put(self)

  private var queryHandlerFunction: Option[PartialFunction[Query, Future[Event]]] = None
  private var commandHandlerFunction: Option[PartialFunction[Command, Future[Event]]] = None
  private var eventHandlerFunction: Option[PartialFunction[Event, Unit]] = None
  private val requestPromises = new mutable.HashMap[String, Promise[Event]]()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    self ! RestoreState(
      queryHandler = queryHandlerFunction,
      commandHandler = commandHandlerFunction,
      eventHandler = eventHandlerFunction,
      requests = requestPromises)
  }

  override def receive: Receive = {
    case askQuery: AskQuery if askQuery.fromServiceName == thisServiceName =>
      val thisRequestId = UUID.randomUUID().toString
      mediator ! Send(path = s"/user/${akka.messenger.api.systemName}", msg = askQuery.copy(requestId = Some(thisRequestId), fromNodeRef = Some(self)), localAffinity = false)
      val promise = Promise[Event]()
      requestPromises += thisRequestId -> promise
      context.system.scheduler.scheduleOnce(askQuery.timeout) {
        self ! TimeoutRequestPromise(requestId = thisRequestId)
      }(context.system.dispatcher)
      sender() ! promise.future

    case answerQuery: AnswerQuery if answerQuery.fromServiceName == thisServiceName =>
      mediator ! Send(path = s"/user/${akka.messenger.api.systemName}", msg = answerQuery, localAffinity = false)

    case tellCommand: TellCommand if tellCommand.fromServiceName == thisServiceName =>
      val thisRequestId = UUID.randomUUID().toString
      mediator ! Send(path = s"/user/${akka.messenger.api.systemName}", msg = tellCommand.copy(requestId = Some(thisRequestId), fromNodeRef = Some(self)), localAffinity = false)
      val promise = Promise[Event]()
      requestPromises += thisRequestId -> promise
      context.system.scheduler.scheduleOnce(tellCommand.timeout) {
        self ! TimeoutRequestPromise(requestId = thisRequestId)
      }(context.system.dispatcher)
      sender() ! promise.future

    case answerCommand: AnswerCommand if answerCommand.fromServiceName == thisServiceName =>
      mediator ! Send(path = s"/user/${akka.messenger.api.systemName}", msg = answerCommand, localAffinity = false)
      self ! NotifyEvent(requestId = answerCommand.requestId, fromServiceName = answerCommand.fromServiceName, event = answerCommand.event)

    case notifyEvent: NotifyEvent if notifyEvent.fromServiceName == thisServiceName =>
      val thisRequestId = notifyEvent.requestId match {
        case Some(requestId) => requestId
        case None => UUID.randomUUID().toString
      }
      mediator ! Send(path = s"/user/${akka.messenger.api.systemName}", msg = notifyEvent.copy(requestId = Some(thisRequestId)), localAffinity = false)

    case SubscribeToServiceEvents(fromServiceName) =>
      mediator ! Subscribe(topic = fromServiceName, self)

    case UnsubscribeFromServiceEvents(fromServiceName) =>
      mediator ! Unsubscribe(topic = fromServiceName, self)

    case askQuery: AskQuery if askQuery.toServiceName == thisServiceName =>
      queryHandlerFunction.foreach { handler =>
        handler(askQuery.query).map { event =>
          self ! AnswerQuery(requestId = askQuery.requestId, fromServiceName = thisServiceName, toServiceName = askQuery.fromServiceName, toNodeRef = askQuery.fromNodeRef, event = event)
        }(context.dispatcher)
      }

    case answerQuery: AnswerQuery if answerQuery.toServiceName == thisServiceName =>
      for {
        requestId <- answerQuery.requestId
        promise <- requestPromises.get(requestId)
      } {
        requestPromises -= requestId
        promise.success(answerQuery.event)
      }

    case tellCommand: TellCommand if tellCommand.toServiceName == thisServiceName =>
      commandHandlerFunction.foreach { handler =>
        handler(tellCommand.command).map { event =>
          self ! AnswerCommand(requestId = tellCommand.requestId, fromServiceName = thisServiceName, toServiceName = tellCommand.fromServiceName, toNodeRef = tellCommand.fromNodeRef, event = event)
        }(context.dispatcher)
      }

    case answerCommand: AnswerCommand if answerCommand.toServiceName == thisServiceName =>
      for {
        requestId <- answerCommand.requestId
        promise <- requestPromises.get(requestId)
      } {
        requestPromises -= requestId
        promise.success(answerCommand.event)
      }

    case TimeoutRequestPromise(requestId) =>
      requestPromises.get(requestId).foreach { promise =>
        requestPromises -= requestId
        promise.failure(RequestTimeout(requestId = requestId))
      }

    case notifyEvent: NotifyEvent if notifyEvent.fromServiceName != thisServiceName =>
      eventHandlerFunction.foreach(handler => handler(notifyEvent.event))

    case InstallQueryHandlerFunction(queryHandler) =>
      queryHandlerFunction = Some(queryHandler)

    case InstallCommandHandlerFunction(commandHandler) =>
      commandHandlerFunction = Some(commandHandler)

    case InstallEventHandlerFunction(eventHandler) =>
      eventHandlerFunction = Some(eventHandler)

    case RestoreState(
    queryHandler,
    commandHandler,
    eventHandler,
    requests) =>
      queryHandlerFunction = queryHandler
      commandHandlerFunction = commandHandler
      eventHandlerFunction = eventHandler
      requestPromises ++= requests
  }
}
