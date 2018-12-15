package akka.messenger.messages

import akka.messenger.api.messages.{Event, Query}

import scala.concurrent.Future

private[messenger] case class InstallQueryHandlerFunction(queryHandler: PartialFunction[Query, Future[Event]])
