package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.GC
import org.quartz.{Job, JobExecutionContext}

sealed trait CollectionJob extends Job

object CollectionJob {
  final class JGit() extends CollectionJob {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap      = context.getJobDetail.getJobDataMap
      val project         = jobDataMap.get(MetricsContext.JGit.projectKey).asInstanceOf[MetricsConf.Project]
      val metric          = jobDataMap.getString(MetricsContext.JGit.metricKey)
      val staticsFunction = jobDataMap.get(MetricsContext.JGit.gcKey).asInstanceOf[Function[Unit, GC.RepoStatistics]]
      val statistics      = staticsFunction.apply()

      println("Running the job again " + statistics)
      Kamon
        .gauge("numberOfPackedObjects")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfPackFiles")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfPackFiles.toDouble)
      Kamon
        .gauge("numberOfLooseObjects")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfLooseObjects.toDouble)
      Kamon
        .gauge("numberOfLooseRefs")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfLooseRefs.toDouble)
      Kamon
        .gauge("numberOfPackedRefs")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfPackedRefs.toDouble)
      Kamon
        .gauge("sizeOfLooseObjects")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.sizeOfLooseObjects.toDouble)
      Kamon
        .gauge("sizeOfPackedObjects")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.sizeOfPackedObjects.toDouble)
      Kamon
        .gauge("numberOfBitmaps")
        .withTag("project", project.path.getAbsolutePath)
        .withTag("metric", metric)
        .update(statistics.numberOfBitmaps.toDouble)
    }
  }
}
