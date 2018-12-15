package queryTest

case class EchoQuery(message: String) extends akka.messenger.api.messages.Query
