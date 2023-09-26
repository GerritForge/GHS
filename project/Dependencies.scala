import sbt._

object Dependencies {
  lazy val logback      = "ch.qos.logback"              % "logback-classic" % "1.4.11"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.4"
  lazy val quartz       = "org.quartz-scheduler"        % "quartz"          % "2.3.2"
  lazy val munit        = "org.scalameta"              %% "munit"           % "0.7.29"
}