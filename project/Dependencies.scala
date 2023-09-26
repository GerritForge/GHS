import sbt._
import scalafix.sbt.ScalafixPlugin.autoImport._

object Dependencies {
  lazy val kamon      = "io.kamon"              %% "kamon-prometheus" % "2.5.9"
  lazy val munit      = "org.scalameta"         %% "munit"            % "0.7.29"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig"       % "0.17.4"
  lazy val quartz     = "org.quartz-scheduler"   % "quartz"           % "2.3.2"
}
