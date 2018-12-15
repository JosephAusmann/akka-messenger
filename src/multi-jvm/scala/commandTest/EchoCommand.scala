package commandTest

case class EchoCommand(message: String) extends akka.messenger.api.messages.Command
