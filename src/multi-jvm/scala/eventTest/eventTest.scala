package eventTest

import akka.actor.{ActorSystem, Props}

object EventTestMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2552)).actorOf(MessengerClusterActor.props(true))
  }
}

object EventTestMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2553)).actorOf(MessengerClusterActor.props(false))
  }
}

object EventTestMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2554)).actorOf(MessengerClusterActor.props(false))
  }
}

object EventTestMultiJvmNode4 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2555)).actorOf(ReceiveEventActor.props(id = 1))
  }
}

object EventTestMultiJvmNode5 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2556)).actorOf(ReceiveEventActor.props(id = 2))
  }
}

object EventTestMultiJvmNode6 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, EventTestConfig.makeConfig(port = 2557)).actorOf(Props[NotifyEventActor])
  }
}