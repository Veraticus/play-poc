name := """play-poc"""

scalaVersion := "2.11.7"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
lazy val akkaVersion = "2.4.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

javaOptions in Universal ++= Seq(
  "-Dcom.sun.management.jmxremote.port=9999",
  "-Dcom.sun.management.jmxremote.authenticate=false",
  "-Dcom.sun.management.jmxremote.ssl=false"
)

dockerBaseImage := "davidcaseria/akka-cluster"
