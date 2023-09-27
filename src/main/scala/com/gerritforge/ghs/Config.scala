package com.gerritforge.ghs

import pureconfig._
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

import Config._

final case class Config(metricsCollection: MetricsCollection, tasks: Tasks)

object Config {
  def apply(metricsPath: String, tasksPath: String): Config = {
    val configE = for {
      metricsPath <- ConfigSource.file(metricsPath).load[MetricsCollection]
      tasksPath   <- ConfigSource.file(tasksPath).load[Tasks]
    } yield Config(metricsPath, tasksPath)
    configE.fold(error => throw ConfigReaderException(error), identity)
  }

  final case class MetricsCollection(projects: List[MetricsCollection.Project])

  object MetricsCollection {
    final case class Project(name: String, frequency: FiniteDuration)
  }

  final case class Tasks(projects: List[Tasks.Project])

  object Tasks {
    final case class Project(name: String, command: String, parameters: String)
  }
}
