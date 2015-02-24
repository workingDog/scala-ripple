sbtPlugin := true

name := "rippleprotocol"

organization := "com.kodekutters"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
 "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
 "org.java-websocket" % "Java-WebSocket" % "1.3.0",
 "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2"
)
