package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import com.gerritforge.ghs.config.MetricsConf.Project
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}

sealed trait ContextMetrics {
  def `type`: String
  def data(project: MetricsConf.Project): Map[String, AnyRef]
}

object ContextMetrics {
  case object JGit extends ContextMetrics {
    val gcKey      = "gc"
    val projectKey = "project"
    val metricKey  = "metric"

    override val `type`: String = "jgit"

    override def data(project: Project): Map[String, AnyRef] = {
      val gc                                     = new GC(new FileRepository(project.path.getAbsolutePath))
      val gcF: Function[Unit, GC.RepoStatistics] = _ => gc.getStatistics
      Map(
        gcKey      -> gcF,
        projectKey -> project.path.getAbsolutePath,
        metricKey  -> ContextMetrics.JGit.`type`
      )
    }
  }
}
