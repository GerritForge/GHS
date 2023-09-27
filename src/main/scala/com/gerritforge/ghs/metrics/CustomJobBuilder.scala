package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import com.gerritforge.ghs.config.MetricsConf.Project
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}

import java.io.File

sealed trait ContextMetrics {
  def `type`: String
  def data(gerritSite: File, project: MetricsConf.Project): Map[String, AnyRef]
}

object ContextMetrics {
  case object JGit extends ContextMetrics {
    val gcKey      = "gc"
    val projectKey = "project"
    val metricKey  = "metric"

    override val `type`: String = "jgit"

    override def data(gerritSite: File, project: Project): Map[String, AnyRef] = {
      val repository                             = gerritSite.getAbsolutePath + "/" + project.name.getName
      val gc                                     = new GC(new FileRepository(repository))
      val gcF: Function[Unit, GC.RepoStatistics] = _ => gc.getStatistics
      Map(
        gcKey      -> gcF,
        projectKey -> project.name.getName,
        metricKey  -> ContextMetrics.JGit.`type`
      )
    }
  }
}
