package com.gerritforge.ghs.metrics

import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.GC
import org.quartz.{Job, JobExecutionContext}

sealed trait CollectionJob extends Job

object CollectionJob {
  final class JGit() extends CollectionJob {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap          = context.getJobDetail.getJobDataMap
      val projectAbsoultePath = jobDataMap.getString(ContextMetrics.JGit.projectKey)
      val metricType          = jobDataMap.getString(ContextMetrics.JGit.metricKey)
      val staticsFunction = jobDataMap.get(ContextMetrics.JGit.gcKey).asInstanceOf[Function[Unit, GC.RepoStatistics]]
      val statistics      = staticsFunction.apply()

      println("Running the job again " + statistics)
      Kamon
        .gauge("numberOfPackedObjects")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfPackFiles")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfPackFiles.toDouble)
      Kamon
        .gauge("numberOfLooseObjects")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfLooseObjects.toDouble)
      Kamon
        .gauge("numberOfLooseRefs")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfLooseRefs.toDouble)
      Kamon
        .gauge("numberOfPackedRefs")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfPackedRefs.toDouble)
      Kamon
        .gauge("sizeOfLooseObjects")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.sizeOfLooseObjects.toDouble)
      Kamon
        .gauge("sizeOfPackedObjects")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.sizeOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfBitmaps")
        .withTag("project", projectAbsoultePath)
        .withTag("metric", metricType)
        .update(statistics.numberOfBitmaps.toDouble)
    }
  }
}
