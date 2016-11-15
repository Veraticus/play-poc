name := """play-poc"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.codacy" %% "scala-consul" % "1.1.0"
)

javaOptions in Universal ++= Seq(
  "-Dcom.sun.management.jmxremote.port=9999",
  "-Dcom.sun.management.jmxremote.authenticate=false",
  "-Dcom.sun.management.jmxremote.ssl=false"
)

dockerBaseImage := "davidcaseria/akka-cluster"
