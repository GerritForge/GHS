package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}

import java.io.File

final case class MetricsData(projectName: String, collectFn: Function[Unit, AnyRef])

object MetricsData {
  def apply(gitSiteBasePath: File, project: Config.MetricsCollection.Project): MetricsData = {
    val repository                             = gitSiteBasePath.getAbsolutePath + "/" + project.name
    val gc                                     = new GC(new FileRepository(repository))
    val gcF: Function[Unit, GC.RepoStatistics] = _ => gc.getStatistics
    MetricsData(project.name, gcF)
  }
}
