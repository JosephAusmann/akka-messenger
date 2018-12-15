package akka.messenger.messages

import akka.actor.ActorRef
import akka.messenger.api.messages.Query
import scala.concurrent.duration.FiniteDuration

private[messenger] case class AskQuery(requestId: Option[String] = None,
                                       fromServiceName: String,
                                       fromNodeRef: Option[ActorRef] = None,
                                       toServiceName: String,
                                       query: Query,
                                       timeout: FiniteDuration)
