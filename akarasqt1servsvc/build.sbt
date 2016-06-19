import com.typesafe.sbt.SbtAspectj.{ Aspectj, aspectjSettings, useInstrumentedClasses }
import com.typesafe.sbt.SbtAspectj.AspectjKeys._

packSettings

Revolver.settings

val sprayV = "1.3.3"

val akkaV = "2.4.7"

val squbsV = "0.8.0"

// TODO This is temporary.  You will delete this line in 0.8.1.
dependencyOverrides += "com.ebay.aero" % "cal-client-impl" % "3.0.1"

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
  "com.ebay.squbs" %% "rocksqubs-kernel" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-paypalcfg" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-perfmon" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-vi" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-paypalpipeline" % squbsV,
  "com.ebay.squbs" %% "rocksqubs-paypalprofile" % squbsV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.squbs" %% "squbs-testkit" % squbsV % "test"
)

aspectjSettings ++ Seq(
  inputs in Aspectj <++= update map { report =>
    report.matching(moduleFilter(organization = "com.typesafe.akka", name = "akka-actor*"))
  },
  fullClasspath in Runtime <<= useInstrumentedClasses(Runtime),
  aspectjVersion    :=  "1.8.5",
  javaOptions in run <++= weaverOptions in Aspectj,
  javaOptions in Revolver.reStart <++= weaverOptions in Aspectj
)

mainClass in (Compile, run) := Some("org.squbs.unicomplex.Bootstrap")
