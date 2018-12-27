name := "akka-messenger"

organization := "com.github.orion-io"

homepage := Some(url("https://github.com/orion-io/akka-messenger"))
scmInfo := Some(ScmInfo(url("https://github.com/orion-io/akka-messenger.git"),
                            "git@github.com:orion-io/akka-messenger.git"))

developers := List(
  Developer(
    "JosephAusmann",
    "Joseph Ausmann",
    "joseph.ausmann.17@gmail.com",
    url("https://github.com/JosephAusmann")))

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

version := "0.1.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.19"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion)

lazy val root = (project in file("."))
  .enablePlugins(MultiJvmPlugin)
  .configs(MultiJvm)
