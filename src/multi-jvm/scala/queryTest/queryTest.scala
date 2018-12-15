package queryTest

import akka.actor.ActorSystem

object QueryTestMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2552)).actorOf(MessengerClusterActor.props(true))
  }
}

object QueryTestMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2553)).actorOf(MessengerClusterActor.props(false))
  }
}

object QueryTestMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2554)).actorOf(MessengerClusterActor.props(false))
  }
}

object QueryTestMultiJvmNode4 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2555)).actorOf(AskActor.props(id = "1"))
  }
}

object QueryTestMultiJvmNode5 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2556)).actorOf(AskActor.props(id = "2"))
  }
}

object QueryTestMultiJvmNode6 {
  def main(args: Array[String]): Unit = {
    ActorSystem(akka.messenger.api.systemName, QueryTestConfig.makeConfig(port = 2557)).actorOf(AnswerActor.props)
  }
}