import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.gerritforge"
ThisBuild / organizationName := "GerritForge"

lazy val root = (project in file("."))
  .settings(
    name := "GHS",
    libraryDependencies ++= Seq(
      kamon,
      logback,
      scalaLogging,
      quartz,
      munit % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.