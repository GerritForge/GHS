import sbt._

object Dependencies {
  lazy val jgit         = "org.eclipse.jgit"            % "org.eclipse.jgit" % "6.7.0.202309050840-r"
  lazy val kamon        = "io.kamon"                   %% "kamon-prometheus" % "2.5.9"
  lazy val logback      = "ch.qos.logback"              % "logback-core"     % "1.4.11"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"    % "3.9.5"
  lazy val quartz       = "org.quartz-scheduler"        % "quartz"           % "2.3.2"
  lazy val munit        = "org.scalameta"              %% "munit"            % "0.7.29"
}
