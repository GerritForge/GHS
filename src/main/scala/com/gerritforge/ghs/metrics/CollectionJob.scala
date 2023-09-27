package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.metrics.MetricsData.FSRecord
import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}
import org.quartz.{Job, JobExecutionContext}

import java.io.File
import java.nio.file.Files
import scala.collection.mutable
import scala.jdk.CollectionConverters.IteratorHasAsScala

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

      println("Running the job again " + repoStatistics)
    }
  }
  final class FS() extends CollectionJob {
    override def metricType: String = "filesystem"

    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap                          = context.getJobDetail.getJobDataMap
      val MetricsData(projectName, collectFn) = jobDataMap.get("data").asInstanceOf[MetricsData]
      val fsRecord                            = collectFn().asInstanceOf[FSRecord]

      Kamon
        .gauge("numberOfFilesCount")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(fsRecord.numberOfFilesCount)

      Kamon
        .gauge("numberOfDirectoriesCount")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(fsRecord.numberOfDirectoriesCount)

      Kamon
        .gauge("numberOfEmptyDirectoriesCount")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(fsRecord.numberOfEmptyDirectoriesCount)

      Kamon
        .gauge("numberOfKeepFilesCount")
        .withTag("project", projectName)
        .withTag("metric", metricType)
        .update(fsRecord.numberOfKeepFilesCount)

      println("Running the job again " + fsRecord)
    }
  }
}
