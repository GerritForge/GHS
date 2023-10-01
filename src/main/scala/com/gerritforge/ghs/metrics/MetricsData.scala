package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import kamon.Kamon

sealed trait MetricsData {
  def collectFn: () => Unit
}

object MetricsData {
  final case class JGitData(project: Config.MetricsCollection.Project) extends MetricsData {
    override def collectFn: () => Unit = () => {
      List(
        ("numberOfPackedObjects", 0),
        ("numberOfPackFiles", 0),
        ("numberOfLooseObjects", 0),
        ("numberOfLooseRefs", 0),
        ("numberOfPackedRefs", 0),
        ("sizeOfLooseObjects", 0),
        ("sizeOfPackedObjects", 0),
        ("numberOfBitmaps", 0)
      ).foreach { case (metricName, metricValue) =>
        Kamon
          .gauge(metricName)
          .withTag("project", project.name)
          .withTag("metric", Metric.JGit.`type`)
          .update(metricValue.toDouble)
      }
    }
  }

  final case class FSData(project: Config.MetricsCollection.Project) extends MetricsData {
    override def collectFn: () => Unit = () => {
      List(
        ("numberOfFilesCount", 0),
        ("numberOfDirectoriesCount", 0),
        ("numberOfEmptyDirectoriesCount", 0),
        ("numberOfKeepFilesCount", 0)
      ).foreach { case (metricName, metricValue) =>
        Kamon
          .gauge(metricName)
          .withTag("project", project.name)
          .withTag("metric", Metric.FileSystem.`type`)
          .update(metricValue.toDouble)
      }
    }
  }
}
