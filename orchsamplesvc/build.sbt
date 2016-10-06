updateOptions := updateOptions.value.withCachedResolution(true)

val sprayV = "1.3.3"

val akkaV = "2.4.10"

val squbsV = "0.8.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  "io.spray" %% "spray-routing-shapeless2" % sprayV,
  "io.spray" %% "spray-http" % sprayV,
  "io.spray" %% "spray-httpx" % sprayV,
  "io.spray" %% "spray-can" % sprayV,
  "io.spray" %% "spray-testkit" % sprayV % "test",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.squbs" %% "squbs-unicomplex" % squbsV,
  "org.squbs" %% "squbs-actormonitor" % squbsV,
  "org.squbs" %% "squbs-pattern" % squbsV,
  "org.squbs" %% "squbs-admin" % squbsV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.squbs" %% "squbs-testkit" % squbsV % "test"
)

mainClass in (Compile, run) := Some("org.squbs.unicomplex.Bootstrap")
