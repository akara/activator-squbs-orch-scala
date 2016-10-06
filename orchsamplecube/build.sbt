val akkaV = "2.4.10"

val squbsV = "0.8.0"

updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "org.squbs" %% "squbs-unicomplex" % squbsV,
  "org.squbs" %% "squbs-pattern" % squbsV,
  "org.squbs" %% "squbs-actorregistry" % squbsV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  "org.squbs" %% "squbs-testkit" % squbsV % "test"
)
