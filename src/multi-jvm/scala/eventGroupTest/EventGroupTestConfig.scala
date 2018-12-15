package eventGroupTest

import com.typesafe.config.{Config, ConfigFactory}

object EventGroupTestConfig {
  val config = ConfigFactory.parseString(
    """
      |akka {
      |  extensions = ["akka.cluster.pubsub.DistributedPubSub"]
      |  actor {
      |    provider = "cluster"
      |  }
      |  remote {
      |    artery {
      |      enabled = on
      |      transport = tcp
      |      canonical.hostname = "127.0.0.1"
      |    }
      |  }
      |  cluster {
      |    pub-sub {
      |      name = distributedPubSubMediator
      |      role = ""
      |      routing-logic = round-robin
      |      gossip-interval = 1s
      |      removed-time-to-live = 120s
      |      max-delta-elements = 3000
      |      send-to-dead-letters-when-no-subscribers = off
      |    }
      |  }
      |}
    """.stripMargin)

  def makeConfig(port: Int): Config = {
    config.withFallback(ConfigFactory.parseString(s"akka.remote.artery.canonical.port=$port"))
  }
}
