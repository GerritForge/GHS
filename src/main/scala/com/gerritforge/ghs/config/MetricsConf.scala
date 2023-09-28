package com.gerritforge.ghs.config

import java.io.File
import scala.concurrent.duration.FiniteDuration

final case class MetricsConf(gerritSite: File, projects: List[MetricsConf.Project])

object MetricsConf {
  final case class Project(name: File, frequency: FiniteDuration)
}
