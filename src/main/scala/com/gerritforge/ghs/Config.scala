package com.gerritforge.ghs

import cats.syntax.apply._
import cats.syntax.either._
import pureconfig._
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

import Config._

final case class Config(metrics: Metrics, tasks: Tasks)

object Config {
  def apply(metricsPath: String, tasksPath: String): Config = {
    (ConfigSource.file(metricsPath).load[Metrics], ConfigSource.file(tasksPath).load[Tasks])
      .mapN(Config.apply)
      .valueOr(error => throw ConfigReaderException(error))
  }

  final case class Metrics(projects: List[Metrics.Project])

  object Metrics {
    final case class Project(name: String, frequency: FiniteDuration)
  }

  final case class Tasks(projects: List[Tasks.Project])

  object Tasks {
    final case class Project(name: String, command: String, parameters: String)
  }
}
