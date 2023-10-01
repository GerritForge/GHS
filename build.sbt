import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.gerritforge"
ThisBuild / organizationName := "GerritForge"

// For scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root = (project in file("."))
  .settings(
    name := "GHS",
    libraryDependencies ++= Seq(
      jgit,
      kamon,
      logback,
      pureConfig,
      quartz,
      scalaLogging,
      cats,
      scalaTest % Test,
      sttp      % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
