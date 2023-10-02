import sbt._
import scalafix.sbt.ScalafixPlugin.autoImport._

object Dependencies {
  lazy val jgit                = "org.eclipse.jgit"               % "org.eclipse.jgit"       % "6.7.0.202309050840-r"
  lazy val kamon               = "io.kamon"                      %% "kamon-prometheus"       % "2.5.9"
  lazy val logback             = "ch.qos.logback"                 % "logback-core"           % "1.4.11"
  lazy val osLib               = "com.lihaoyi"                   %% "os-lib"                 % "0.9.1"
  lazy val pureConfig          = "com.github.pureconfig"         %% "pureconfig"             % "0.17.4"
  lazy val quartz              = "org.quartz-scheduler"           % "quartz"                 % "2.3.2"
  lazy val scalaLogging        = "com.typesafe.scala-logging"    %% "scala-logging"          % "3.9.5"
  lazy val scalaTest           = "org.scalatest"                 %% "scalatest"              % "3.2.17"
  lazy val sttp                = "com.softwaremill.sttp.client3" %% "core"                   % "3.9.0"
  lazy val tapir               = "com.softwaremill.sttp.tapir"   %% "tapir-core"             % V.tapir
  lazy val tapirNetty          = "com.softwaremill.sttp.tapir"   %% "tapir-netty-server"     % V.tapir
  lazy val tapirSttpStubServer = "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server" % V.tapir

  object V {
    val tapir = "1.7.5"
  }
}
