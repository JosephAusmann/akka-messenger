package eventGroupTest

import akka.actor.{ActorSystem, Props}

object EventGroupTestMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2552)).actorOf(MessengerClusterActor.props(true))
  }
}

object EventGroupTestMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2553)).actorOf(MessengerClusterActor.props(false))
  }
}

object EventGroupTestMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2554)).actorOf(MessengerClusterActor.props(false))
  }
}

object EventGroupTestMultiJvmNode4 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2555)).actorOf(ReceiveEventActor.props(id = 1))
  }
}

object EventGroupTestMultiJvmNode5 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2556)).actorOf(ReceiveEventActor.props(id = 2))
  }
}

object EventGroupTestMultiJvmNode6 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventGroupTestConfig.makeConfig(port = 2557)).actorOf(Props[NotifyEventActor])
  }
}