package akka.messenger.messages

private[messenger] case class SubscribeToServiceEvents(fromServiceName: String, group: Option[String])
