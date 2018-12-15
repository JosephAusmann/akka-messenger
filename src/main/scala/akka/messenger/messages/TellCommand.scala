package akka.messenger.messages

import akka.actor.ActorRef
import akka.messenger.api.messages.Command
import scala.concurrent.duration.FiniteDuration

private[messenger] case class TellCommand(requestId: Option[String] = None,
                                          fromServiceName: String,
                                          fromNodeRef: Option[ActorRef] = None,
                                          toServiceName: String,
                                          command: Command,
                                          timeout: FiniteDuration)
