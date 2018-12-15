package akka.messenger.messages

import akka.messenger.api.messages.Event

private[messenger] case class InstallEventHandlerFunction(eventHandler: PartialFunction[Event, Unit])
