package eventTest

case class CounterEvent(counter: Int) extends akka.messenger.api.messages.Event
