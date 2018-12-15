package akka.messenger.messages

import akka.messenger.api.messages.Event

private[messenger] case class NotifyEvent(requestId: Option[String] = None,
                                          fromServiceName: String,
                                          event: Event)
