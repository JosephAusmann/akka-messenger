package commandTest

case class EchoEvent(message: String) extends akka.messenger.api.messages.Event