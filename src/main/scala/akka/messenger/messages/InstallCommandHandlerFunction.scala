package akka.messenger.messages

import akka.messenger.api.messages.{Command, Event}

import scala.concurrent.Future

private[messenger] case class InstallCommandHandlerFunction(commandHandler: PartialFunction[Command, Future[Event]])
