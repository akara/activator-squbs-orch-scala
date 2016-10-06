scalaVersion in ThisBuild := "2.11.8"

name := "orchsample"

organization in ThisBuild := "org.squbs"

version in ThisBuild := "0.0.1-SNAPSHOT"

publishArtifact in ThisBuild := false

checksums in ThisBuild := Nil

fork in ThisBuild := true

lazy val orchsamplemsgs = project

lazy val orchsamplecube = project dependsOn orchsamplemsgs

lazy val orchsamplesvc = project dependsOn (orchsamplemsgs, orchsamplecube)