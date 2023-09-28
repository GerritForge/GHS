import org.typelevel.scalacoptions.ScalacOptions
import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.gerritforge"
ThisBuild / organizationName := "GerritForge"

// For scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

// This makes the tpolecat plugin and scalatest play nice
// https://github.com/typelevel/sbt-tpolecat#scalatest-warnings
Test / tpolecatExcludeOptions += ScalacOptions.warnNonUnitStatement

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
      tapir,
      tapirNetty,
      scalaTest           % Test,
      sttp                % Test,
      tapirSttpStubServer % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
