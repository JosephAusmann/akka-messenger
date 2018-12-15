package commandTest

import akka.actor.ActorSystem

object CommandTestMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2552)).actorOf(MessengerClusterActor.props(true))
  }
}

object CommandTestMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2553)).actorOf(MessengerClusterActor.props(false))
  }
}

object CommandTestMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2554)).actorOf(MessengerClusterActor.props(false))
  }
}

object CommandTestMultiJvmNode4 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2555)).actorOf(TellActor.props(id = "1"))
  }
}

object CommandTestMultiJvmNode5 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2556)).actorOf(TellActor.props(id = "2"))
  }
}

object CommandTestMultiJvmNode6 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, CommandTestConfig.makeConfig(port = 2557)).actorOf(AnswerActor.props)
  }
}
