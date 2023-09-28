package com.gerritforge.ghs

import scala.concurrent.duration.FiniteDuration

final case class MetricsCollection(projects: List[MetricsCollection.Project])

object MetricsCollection {
  final case class Project(name: String, frequency: FiniteDuration)
}
