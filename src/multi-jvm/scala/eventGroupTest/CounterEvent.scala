package eventGroupTest

case class CounterEvent(counter: Int) extends akka.messenger.api.messages.Event
