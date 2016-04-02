checksums in update := Nil

resolvers ++= Seq(
  "paypal Central Public" at "https://paypalcentral.es.paypalcorp.com/nexus/content/groups/public/",
  "Maven Central Proxy" at "https://paypalcentral.es.paypalcorp.com/nexus/content/repositories/central/"
)

addSbtPlugin("com.ebay.squbs" % "sbt-ebay" % "0.7.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.9.4")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.7.4")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")
