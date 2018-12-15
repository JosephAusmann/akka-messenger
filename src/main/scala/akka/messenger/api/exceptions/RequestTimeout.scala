package akka.messenger.api.exceptions

case class RequestTimeout(requestId: String) extends Exception
