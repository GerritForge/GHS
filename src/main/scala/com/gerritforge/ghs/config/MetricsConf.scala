package com.gerritforge.ghs.config

import scala.concurrent.duration.FiniteDuration

final case class MetricsConf(projects: List[MetricsConf.Project])

object MetricsConf {
  final case class Project(name: String, frequency: FiniteDuration)
}
