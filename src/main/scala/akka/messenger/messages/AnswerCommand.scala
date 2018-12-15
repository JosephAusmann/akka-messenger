package akka.messenger.messages

import akka.actor.ActorRef
import akka.messenger.api.messages.Event

private[messenger] case class AnswerCommand(requestId: Option[String] = None,
                                            fromServiceName: String,
                                            toServiceName: String,
                                            toNodeRef: Option[ActorRef] = None,
                                            event: Event)
