package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}

import java.io.File

sealed trait MetricsData {
  def collectFn: () => Unit
}

object MetricsData {
  final case class JGitData(gitSiteBasePath: File, project: Config.MetricsCollection.Project) extends MetricsData {
    private val repository = gitSiteBasePath.getAbsolutePath + "/" + project.name
    private val gc         = new GC(new FileRepository(repository))
    override def collectFn: () => Unit = () => {
      val repoStatistics = gc.getStatistics

      List(
        ("numberOfPackedObjects", repoStatistics.numberOfPackedObjects),
        ("numberOfPackFiles", repoStatistics.numberOfPackFiles),
        ("numberOfLooseObjects", repoStatistics.numberOfLooseObjects),
        ("numberOfLooseRefs", repoStatistics.numberOfLooseRefs),
        ("numberOfPackedRefs", repoStatistics.numberOfPackedRefs),
        ("sizeOfLooseObjects", repoStatistics.sizeOfLooseObjects),
        ("sizeOfPackedObjects", repoStatistics.sizeOfPackedObjects),
        ("numberOfBitmaps", repoStatistics.numberOfBitmaps)
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
