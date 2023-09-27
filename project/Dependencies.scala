import sbt._

object Dependencies {
  lazy val quartz = "org.quartz-scheduler" % "quartz" % "2.3.2"
  lazy val munit  = "org.scalameta"       %% "munit"  % "0.7.29"
  lazy val kamon = "io.kamon" %% "kamon-prometheus" % "2.5.9"
}
