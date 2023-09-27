import sbt._
import scalafix.sbt.ScalafixPlugin.autoImport._

object Dependencies {
  lazy val jgit         = "org.eclipse.jgit"               % "org.eclipse.jgit" % "6.7.0.202309050840-r"
  lazy val kamon        = "io.kamon"                      %% "kamon-prometheus" % "2.5.9"
  lazy val logback      = "ch.qos.logback"                 % "logback-core"     % "1.4.11"
  lazy val pureConfig   = "com.github.pureconfig"         %% "pureconfig"       % "0.17.4"
  lazy val quartz       = "org.quartz-scheduler"           % "quartz"           % "2.3.2"
  lazy val scalaLogging = "com.typesafe.scala-logging"    %% "scala-logging"    % "3.9.5"
  lazy val scalaTest    = "org.scalatest"                 %% "scalatest"        % "3.2.17"
  lazy val sttp         = "com.softwaremill.sttp.client3" %% "core"             % "3.9.0"
}
