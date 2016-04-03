import _root_.sbt.Keys._

val akkaV = "2.3.13"

val squbsV = "0.7.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "org.squbs" %% "squbs-unicomplex" % squbsV,
  "org.squbs" %% "squbs-pattern" % squbsV,
  "org.squbs" %% "squbs-actorregistry" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-kernel" % squbsV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  "org.squbs" %% "squbs-testkit" % squbsV % "test"
)
