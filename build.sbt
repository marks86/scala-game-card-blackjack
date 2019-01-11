name := "scala-game-blackjack"

version := "1.0"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.19"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.lihaoyi" %% "pprint" % "0.5.3"
)
