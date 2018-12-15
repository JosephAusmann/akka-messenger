package akka.messenger.api

import akka.actor.{ActorRef, ActorSystem}
import akka.messenger.actors.ConnectorActor
import akka.messenger.messages._
import akka.pattern.ask
import akka.util.Timeout
import messages.{Command, Event, Query}
import scala.concurrent.{ExecutionContext, Future}

object Connector {
  def make(serviceName: String)(implicit system: ActorSystem): Connector = {
    new Connector(serviceName, system.actorOf(ConnectorActor.props(serviceName), serviceName))
  }
}

final class Connector private(private val thisServiceName: String,
                              private val connectorRef: ActorRef) {

  def askQuery(toServiceName: String, query: Query)(implicit timeout: concurrent.duration.FiniteDuration, ec: ExecutionContext): Future[Event] = {
    implicit val resultTimeout: akka.util.Timeout = Timeout(timeout)
    (connectorRef ? AskQuery(fromServiceName = thisServiceName, toServiceName = toServiceName, query = query, timeout = timeout)).mapTo[Future[Event]].flatten
  }

  def tellCommand(toServiceName: String, command: Command)(implicit timeout: concurrent.duration.FiniteDuration, ec: ExecutionContext): Future[Event] = {
    implicit val resultTimeout: akka.util.Timeout = Timeout(timeout)
    (connectorRef ? TellCommand(fromServiceName = thisServiceName, toServiceName = toServiceName, command = command, timeout = timeout)).mapTo[Future[Event]].flatten
  }

  def notifyEvent(event: Event): Unit = {
    connectorRef ! NotifyEvent(fromServiceName = thisServiceName, event = event)
  }

  def subscribeToServiceEvents(serviceName: String, group: Option[String] = None): Unit = {
    connectorRef ! SubscribeToServiceEvents(serviceName, group)
  }

  def unsubscribeFromServiceEvents(serviceName: String): Unit = {
    connectorRef ! UnsubscribeFromServiceEvents(serviceName)
  }

  def installQueryHandlerFunction(queryHandler: PartialFunction[Query, Future[Event]]): Unit = {
    connectorRef ! InstallQueryHandlerFunction(queryHandler)
  }

  def installCommandHandlerFunction(commandHandler: PartialFunction[Command, Future[Event]]): Unit = {
    connectorRef ! InstallCommandHandlerFunction(commandHandler)
  }

  def installEventHandlerFunction(eventHandler: PartialFunction[Event, Unit]): Unit = {
    connectorRef ! InstallEventHandlerFunction(eventHandler)
  }
}
