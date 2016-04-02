scalaVersion in ThisBuild := "2.11.7"

name := "akarasqt1serv"

organization in ThisBuild := "com.paypal.myorg"

version in ThisBuild := "0.0.1-SNAPSHOT"

import com.squbs.sbt.paypal._
deploymentType in ThisBuild := MidTier

publishArtifact in ThisBuild := false

checksums in ThisBuild := Nil

fork in ThisBuild := true

// For metadata registration
teamDL := "asucharitakul@paypal.com"

lazy val akarasqt1servmsgs = project

lazy val akarasqt1servcube = project dependsOn akarasqt1servmsgs

lazy val akarasqt1servsvc = project dependsOn (akarasqt1servmsgs, akarasqt1servcube)

// In order to let developers fully control the dependency manangement, we disable the feature in app level.
// For the known issue of enabling this feaure, please refer to
// https://github.paypal.com/Squbs/sbt-ebay/blob/RELEASE-0.6.X/README.md#ebay-dependencies-managed-by-raptor-2x
disableRaptorDepsMgmt in ThisBuild := true
