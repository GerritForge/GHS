package com.gerritforge.ghs.metrics

import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.GC
import org.quartz.{Job, JobExecutionContext}

sealed trait CollectionJob extends Job {
  def metricType: String
}

object CollectionJob {
  final class JGit() extends CollectionJob {

    override val metricType: String = "jgit"

    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap                          = context.getJobDetail.getJobDataMap
      val MetricsData(projectName, collectFn) = jobDataMap.get("data").asInstanceOf[MetricsData]
      val repoStatistics                      = collectFn().asInstanceOf[GC.RepoStatistics]

      println("Running the job again " + repoStatistics)
      Kamon
        .gauge("numberOfPackedObjects")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfPackFiles")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfPackFiles.toDouble)
      Kamon
        .gauge("numberOfLooseObjects")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfLooseObjects.toDouble)
      Kamon
        .gauge("numberOfLooseRefs")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfLooseRefs.toDouble)
      Kamon
        .gauge("numberOfPackedRefs")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfPackedRefs.toDouble)
      Kamon
        .gauge("sizeOfLooseObjects")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.sizeOfLooseObjects.toDouble)
      Kamon
        .gauge("sizeOfPackedObjects")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.sizeOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfBitmaps")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(repoStatistics.numberOfBitmaps.toDouble)
    }
  }
}